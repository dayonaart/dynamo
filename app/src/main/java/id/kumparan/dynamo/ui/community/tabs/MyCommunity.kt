package id.kumparan.dynamo.ui.community.tabs

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
import id.kumparan.dynamo.model.ListThreadModelViewModel
import id.kumparan.dynamo.model.MyCommunityModelViewModel
import id.kumparan.dynamo.model.UserModel
import id.kumparan.dynamo.model.UserViewModel
import id.kumparan.dynamo.ui.community.rv.CommunityRVAdapter
import id.kumparan.dynamo.utility.ModelInjector
import id.kumparan.dynamo.utility.Utility
import kotlinx.android.synthetic.main.fragment_community_tab_my_community.*
import kotlinx.android.synthetic.main.fragment_home_tab_my_community.pbProgress

class MyCommunity : Fragment() {
    private val myCommunityFactory = ModelInjector.provideListMyCommunityViewModeFactory()
    private val userFactory = ModelInjector.provideUserViewModelFactory()
    private val listAllThreadFactory = ModelInjector.provideListThreadViewModelFactory()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_community_tab_my_community, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val registerActivity = Intent(requireContext(), RegisterActivity::class.java)
        if (userModel()?.id == null) {
            optionBtnCommunity.text = resources.getText(R.string.join_now)
            pbProgress.visibility = View.GONE
            optionBtnCommunity.setOnClickListener {
                startActivity(registerActivity)
            }
            notRegisterLayout.visibility = View.VISIBLE
        } else {
            initUI()
        }
    }

    private fun userModel(): UserModel? {
        val observer = ViewModelProvider(this, userFactory).get(UserViewModel::class.java)
        return observer.getData().value?.data
    }

    private fun listThreadModel(): ListThreadModelViewModel {
        return ViewModelProvider(this, listAllThreadFactory).get(ListThreadModelViewModel::class.java)
    }

    private fun myCommunityModel(): MyCommunityModelViewModel {
        return ViewModelProvider(
            this,
            myCommunityFactory
        ).get(MyCommunityModelViewModel::class.java)
    }

    private fun initUI() {
        myCommunityModel().getData().observe(viewLifecycleOwner, Observer { its ->
            when {
                its.isNotEmpty() -> {
                    pbProgress.visibility = View.GONE
                    notRegisterLayout.visibility = View.GONE
                    val communityAdapter = CommunityRVAdapter(its)
                    rvCommunityMyCommunity.apply {
                        layoutManager = LinearLayoutManager(context)
                        adapter = communityAdapter
                    }
                }
                else -> {
                    optionBtnCommunity.text = resources.getText(R.string.explore_community)
                    pbProgress.visibility = View.VISIBLE
                    ApiUtility().getAllThread(listThreadModel())
                    ApiUtility().getMyCommunity(myCommunityModel(), userModel()?.id!!){message->Utility.customToast(requireContext(),message)}
                    pbProgress.visibility = View.GONE

                }
            }
        })
    }
}

