package id.kumparan.dynamo.ui.home.tabs

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import id.kumparan.dynamo.R
import id.kumparan.dynamo.RegisterActivity
import id.kumparan.dynamo.ReportThreadActivity
import id.kumparan.dynamo.api.ApiUtility
import id.kumparan.dynamo.localstorage.LocalStorage
import id.kumparan.dynamo.model.*
import id.kumparan.dynamo.ui.home.rv.MyCommunityRVAdapter
import id.kumparan.dynamo.utility.ModelInjector
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
        val localUser = LocalStorage.readLocalToModel(requireContext())
        if (localUser?.data?.email == null) {
            val registerActivity = Intent(requireContext(), RegisterActivity::class.java)
            signUpBtn.setOnClickListener {
                startActivity(registerActivity)
            }
            errorLayout.visibility = View.VISIBLE
        } else {
            signUpBtn.visibility = View.GONE
            myThreadModel().getData().observe(viewLifecycleOwner, Observer {
                val data = it.sortedByDescending { d ->
                    d.createdAt
                }
                val communityAdapter =
                    MyCommunityRVAdapter(data,
                        object : MyCommunityRVAdapter.OptionsMenuClickListener {
                            override fun onOptionsMenuClicked(position: Int) {
                                performOptionsMenuClick(position, data)
                            }

                        })
                rvHomeMyCommunity.apply {
                    layoutManager = LinearLayoutManager(context)
                    adapter = communityAdapter
                }
                if (it.isNotEmpty()) {
                    errorLayout.visibility = View.GONE
                    pbProgress.visibility = View.GONE
                } else {
//                    GlobalScope.launch{
//                        delay(10000L)
                    pbProgress.visibility = View.VISIBLE
                    ApiUtility().getMyThread(
                        myThreadModel(), userModel()?.id
                        !!
                    )
                    ApiUtility().getAllComment(allCommentListModel())
//                    }
                    pbProgress.visibility = View.GONE
                }
            })
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

    private fun performOptionsMenuClick(position: Int, data: List<MyListThreadModel>) {
        val reportThreadActivity = Intent(requireContext(), ReportThreadActivity::class.java)
        reportThreadActivity.putExtra("data", data[position])
        val popupMenu =
            PopupMenu(requireContext(), rvHomeMyCommunity[position].findViewById(R.id.optionBtn))
        popupMenu.inflate(R.menu.option_menu_thread)
        popupMenu.setOnMenuItemClickListener(object : PopupMenu.OnMenuItemClickListener {
            override fun onMenuItemClick(item: MenuItem?): Boolean {
                when (item?.itemId) {
                    R.id.delete -> {
                        startActivity(reportThreadActivity)
                        return true
                    }
                }
                return false
            }
        })
        popupMenu.show()
    }

    // on destroy of view make the binding reference to null
    override fun onDestroy() {
        super.onDestroy()
//        _binding = null
    }

}

