package id.kumparan.dynamo.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.tabs.TabLayout
import id.kumparan.dynamo.pages.CreateThreadActivity
import id.kumparan.dynamo.LoginActivity
import id.kumparan.dynamo.R
import id.kumparan.dynamo.localstorage.LocalStorage
import id.kumparan.dynamo.model.UserListModelViewModel
import id.kumparan.dynamo.model.UserModel
import id.kumparan.dynamo.model.UserViewModel
import id.kumparan.dynamo.pages.SearchActivity
import id.kumparan.dynamo.ui.home.adapter.HomeTabAdapter
import id.kumparan.dynamo.utility.ModelInjector
import kotlinx.android.synthetic.main.fragment_home.*


class HomeFragment : Fragment() {
    private val userFactory = ModelInjector.provideUserViewModelFactory()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val loginActivity = Intent(requireContext(), LoginActivity::class.java)
        val searchActivity = Intent(requireContext(), SearchActivity::class.java)
        val createThread=Intent(requireContext(), CreateThreadActivity::class.java)
        createThreadFAB.setOnClickListener{
            startActivity(createThread)
        }

        searchBtn.setOnClickListener { _ ->
            startActivity(searchActivity)
        }
        if (userModel()?.id != null) {
            loginBtn.visibility = View.GONE
            createThreadFAB.visibility=View.VISIBLE
//            threadCountLayout.visibility = View.VISIBLE
        } else {
            createThreadFAB.visibility=View.GONE
            loginBtn.setOnClickListener {
                startActivity(loginActivity)
            }
        }
//        userListDataViewModel.getData().observe(viewLifecycleOwner, {
//            threadCount.text = "${it.size ?: "..."}"
//        })
        val adapter = HomeTabAdapter(childFragmentManager, 2)
        homeViewPager.adapter = adapter
        homeViewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(homeTab))
        homeViewPager.offscreenPageLimit = 2
        homeTab.setupWithViewPager(homeViewPager)
        homeTab.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                homeViewPager.currentItem = tab.position
            }
            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

    }
    private fun userModel(): UserModel? {
        val observer = ViewModelProvider(this, userFactory).get(UserViewModel::class.java)
        return observer.getData().value?.data
    }

}