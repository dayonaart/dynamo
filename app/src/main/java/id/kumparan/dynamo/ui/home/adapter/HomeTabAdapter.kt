package id.kumparan.dynamo.ui.home.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import id.kumparan.dynamo.ui.home.tabs.MyCommunity
import id.kumparan.dynamo.ui.home.tabs.Popular

class HomeTabAdapter (fm : FragmentManager, private val fragmentCount : Int): FragmentStatePagerAdapter(fm){

    private val fragmentTitleList = mutableListOf("Komunitas Saya","Populer")

    override fun getItem(position:Int): Fragment{

        return when(position){
            0-> MyCommunity()
            1-> Popular()
            else -> MyCommunity()
        }
    }

    override fun getPageTitle(position: Int):CharSequence?{
        return fragmentTitleList[position]
    }
    override fun getCount(): Int = fragmentCount
}