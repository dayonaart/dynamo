package id.kumparan.dynamo.pages.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import id.kumparan.dynamo.model.AllCommentListModel
import id.kumparan.dynamo.pages.detail_thread_tabs.Comments
import id.kumparan.dynamo.pages.detail_thread_tabs.NewComments
import id.kumparan.dynamo.pages.search_tabs.Profile

class ThreadTabAdapter(fm : FragmentManager, private val fragmentCount : Int,private val threadId:Int?): FragmentStatePagerAdapter(fm) {
    private val fragmentTitleList = mutableListOf("Komentar","Terbaru")

    override fun getItem(position:Int): Fragment {

        return when(position){
            0-> Comments(threadId)
            1-> NewComments()
            else -> Comments(threadId)
        }
    }

    override fun getPageTitle(position: Int):CharSequence?{
        return fragmentTitleList[position]
    }
    override fun getCount(): Int = fragmentCount
}