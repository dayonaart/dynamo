package id.kumparan.dynamo.pages.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import id.kumparan.dynamo.pages.detail_thread_tabs.Comments
import id.kumparan.dynamo.pages.detail_thread_tabs.NewComments
import id.kumparan.dynamo.pages.search_tabs.Profile

class ThreadTabAdapter(fm : FragmentManager, private val fragmentCount : Int): FragmentStatePagerAdapter(fm) {
    private val fragmentTitleList = mutableListOf("Komentar","Terbaru")

    override fun getItem(position:Int): Fragment {

        return when(position){
            0-> Comments()
            1-> NewComments()
            else -> Comments()
        }
    }

    override fun getPageTitle(position: Int):CharSequence?{
        return fragmentTitleList[position]
    }
    override fun getCount(): Int = fragmentCount
}