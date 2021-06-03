package id.kumparan.dynamo.pages.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager2.adapter.FragmentStateAdapter
import id.kumparan.dynamo.model.AllCommentListModel
import id.kumparan.dynamo.pages.detail_thread_tabs.Comments
import id.kumparan.dynamo.pages.detail_thread_tabs.NewComments
import id.kumparan.dynamo.pages.search_tabs.Communities
import id.kumparan.dynamo.pages.search_tabs.Profile
import id.kumparan.dynamo.pages.search_tabs.Thread

class ThreadTabAdapter(fm: FragmentActivity, private val threadId:Int?): FragmentStateAdapter(fm) {
    override fun getItemCount(): Int=2
    override fun createFragment(position: Int): Fragment {
        return when(position){
            0-> Comments(threadId)
            1-> NewComments()
            else -> Comments(threadId)
        }
    }
}