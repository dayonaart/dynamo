package id.kumparan.dynamo.pages.adapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager2.adapter.FragmentStateAdapter
import id.kumparan.dynamo.pages.search_tabs.Communities
import id.kumparan.dynamo.pages.search_tabs.Profile
import id.kumparan.dynamo.pages.search_tabs.Thread


class SearchTabAdapter(fm: FragmentActivity) :
    FragmentStateAdapter(fm) {
    override fun getItemCount(): Int=3
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> Thread()
            1 -> Communities()
            else -> Profile()
        }
    }

}