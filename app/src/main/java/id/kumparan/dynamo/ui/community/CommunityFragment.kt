package id.kumparan.dynamo.ui.community

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout
import id.kumparan.dynamo.R
import id.kumparan.dynamo.ui.community.adapter.CommunityTabAdapter
import kotlinx.android.synthetic.main.fragment_community.*

class CommunityFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_community, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val adapter = CommunityTabAdapter(childFragmentManager, 2)
        communityViewPager.adapter = adapter
        communityViewPager.addOnPageChangeListener(
            TabLayout.TabLayoutOnPageChangeListener(
                communityTab
            )
        )
        communityViewPager.offscreenPageLimit = 2
        communityTab.setupWithViewPager(communityViewPager)
        communityTab.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                communityViewPager.currentItem = tab.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

    }
}