package id.kumparan.dynamo.pages.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.lifecycle.LiveData
import androidx.viewpager2.adapter.FragmentStateAdapter
import id.kumparan.dynamo.model.CommunityListModel
import id.kumparan.dynamo.pages.detail_community_tabs.Moderator
import id.kumparan.dynamo.pages.detail_community_tabs.Rules
import id.kumparan.dynamo.pages.detail_community_tabs.Threads

class DetailCommunityViewPager(
    fm: FragmentActivity,
    private val communityListLiveData: LiveData<CommunityListModel?>
) : FragmentStateAdapter(fm) {
    override fun getItemCount(): Int = 3
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> Threads(communityListLiveData)
            1 -> Rules(communityListLiveData)
            else -> Moderator(communityListLiveData.value?.id)
        }
    }

}
