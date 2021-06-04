package id.kumparan.dynamo.ui.home.tabs

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import id.kumparan.dynamo.R
import id.kumparan.dynamo.RegisterActivity
import id.kumparan.dynamo.api.ApiUtility
import id.kumparan.dynamo.model.AllCommentListModelViewModel
import id.kumparan.dynamo.model.MyListThreadModelViewModel
import id.kumparan.dynamo.model.UserModel
import id.kumparan.dynamo.model.UserViewModel
import id.kumparan.dynamo.ui.home.rv.MyCommunityRVAdapter
import id.kumparan.dynamo.utility.ModelInjector
import id.kumparan.dynamo.utility.Utility
import kotlinx.android.synthetic.main.fragment_home_tab_my_community.*

class MyCommunity : Fragment() {
    private val myListThreadFactory = ModelInjector.provideMyListThreadViewModelFactory()
    private val userFactory = ModelInjector.provideUserViewModelFactory()
    private val allCommentFactory = ModelInjector.provideAllCommentListViewModelFactory()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home_tab_my_community, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (userModel()?.email == null) {
            val registerActivity = Intent(requireContext(), RegisterActivity::class.java)
            signUpBtn.setOnClickListener {
                startActivity(registerActivity)
            }
            errorLayout.visibility = View.VISIBLE
            refreshContainer.visibility = View.GONE
        } else {
            initRefresh()
            refreshContainer.visibility = View.VISIBLE
            signUpBtn.visibility = View.GONE
            myThreadModel().getData().observe(viewLifecycleOwner, Observer {
                errorLayout.visibility = View.VISIBLE
                val data = it.sortedByDescending { d ->
                    d.createdAt
                }
                val communityAdapter =
                    MyCommunityRVAdapter(data, userModel()?.id!!) { data ->
                        ApiUtility().upVoteThread(data.id!!, userModel()?.id!!) { message ->
                            Utility.customToast(requireContext(), message)
                            ApiUtility().getMyThread(myThreadModel(),userModel()?.id!!)
                        }
                    }
                rvHomeMyCommunity.apply {
                    layoutManager = LinearLayoutManager(context)
                    adapter = communityAdapter
                }
                if (it.isNotEmpty()) {
                    errorLayout.visibility = View.GONE
                    pbProgress.visibility = View.GONE
                } else {
                    ApiUtility().getMyThread(
                        myThreadModel(), userModel()?.id
                        !!
                    )
                    ApiUtility().getAllComment(allCommentListModel())
                    pbProgress.visibility = View.GONE
                }
            })
        }
    }

    private fun initRefresh() {
        refreshContainer.setOnRefreshListener {
            ApiUtility().getMyThread(
                myThreadModel(), userModel()?.id
                !!
            )
            ApiUtility().getAllComment(allCommentListModel())
            refreshContainer.isRefreshing = false
        }
    }

    private fun userModel(): UserModel? {
        val observer = ViewModelProvider(this, userFactory).get(UserViewModel::class.java)
        return observer.getData().value?.data
    }

    private fun allCommentListModel(): AllCommentListModelViewModel {
        return ViewModelProvider(
            this,
            allCommentFactory
        ).get(AllCommentListModelViewModel::class.java)

    }

    private fun myThreadModel(): MyListThreadModelViewModel {
        return ViewModelProvider(
            this,
            myListThreadFactory
        ).get(MyListThreadModelViewModel::class.java)
    }
}

