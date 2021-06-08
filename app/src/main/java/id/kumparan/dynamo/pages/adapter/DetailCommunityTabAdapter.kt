package id.kumparan.dynamo.pages.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LiveData
import androidx.viewpager2.adapter.FragmentStateAdapter
import id.kumparan.dynamo.model.CommunityListModel
import id.kumparan.dynamo.pages.detail_community_tabs.Moderator
import id.kumparan.dynamo.pages.detail_community_tabs.Rules
import id.kumparan.dynamo.pages.detail_community_tabs.Threads

class DetailCommunityViewPager(
    fm: FragmentActivity,
    private val communityListLiveData: LiveData<CommunityListModel?>,
    private val isModerator: Boolean
) : FragmentStateAdapter(fm) {
    override fun getItemCount(): Int = 3
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> Threads(communityListLiveData,isModerator)
            1 -> Rules(communityListLiveData,isModerator)
            else -> Moderator(communityListLiveData.value?.id)
        }
    }

}
