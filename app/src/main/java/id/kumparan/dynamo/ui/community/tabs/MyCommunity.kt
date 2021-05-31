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
import id.kumparan.dynamo.model.MyCommunityModelViewModel
import id.kumparan.dynamo.model.UserModel
import id.kumparan.dynamo.model.UserViewModel
import id.kumparan.dynamo.ui.community.rv.CommunityRVAdapter
import id.kumparan.dynamo.utility.ModelInjector
import kotlinx.android.synthetic.main.fragment_community_tab_my_community.*
import kotlinx.android.synthetic.main.fragment_home_tab_my_community.pbProgress
import kotlinx.android.synthetic.main.fragment_home_tab_my_community.rvMyCommunity

class MyCommunity : Fragment() {
    private val myCommunityFactory = ModelInjector.provideListMyCommunityViewModeFactory()
    private val userFactory = ModelInjector.provideUserViewModelFactory()
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
        val myCommunityModel =
            ViewModelProvider(this, myCommunityFactory).get(MyCommunityModelViewModel::class.java)
        myCommunityModel.getData().observe(viewLifecycleOwner, Observer { its ->
            val communityAdapter = CommunityRVAdapter(its)
            rvMyCommunity.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = communityAdapter
            }
            when {
                its.isNotEmpty() -> {
                    pbProgress.visibility = View.GONE
                    haveNoCommunity.visibility = View.GONE
                }
                userModel()?.id == null -> {
                    userNotLoginDescription.text = resources.getText(R.string.come_register)
                    optionBtnCommunity.text = resources.getText(R.string.join_now)
                    optionBtnCommunity.setOnClickListener {
                        startActivity(registerActivity)
                    }
                    pbProgress.visibility = View.GONE
                    haveNoCommunity.visibility = View.VISIBLE
                }
                else -> {
                    userNotLoginDescription.text = resources.getText(R.string.come_join_community)
                    optionBtnCommunity.text = resources.getText(R.string.explore_community)
                    optionBtnCommunity.setOnClickListener {
//                        val registerActivity=Intent(requireContext(),RegisterActivity::class.java)
//                        startActivity(registerActivity)
                    }
                    pbProgress.visibility = View.VISIBLE
                    ApiUtility().getMyCommunity(myCommunityModel, userModel()?.id!!)
                    pbProgress.visibility = View.GONE

                }
            }
        })
    }

    private fun userModel(): UserModel? {
        val observer = ViewModelProvider(this, userFactory).get(UserViewModel::class.java)
        return observer.getData().value?.data
    }
}

