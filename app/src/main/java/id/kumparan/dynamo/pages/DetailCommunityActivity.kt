package id.kumparan.dynamo.pages

import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.map
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import id.kumparan.dynamo.R
import id.kumparan.dynamo.model.CommunityListModel
import id.kumparan.dynamo.model.CommunityListModelViewModel
import id.kumparan.dynamo.model.MyCommunityModel
import id.kumparan.dynamo.model.UserViewModel
import id.kumparan.dynamo.pages.adapter.DetailCommunityViewPager
import id.kumparan.dynamo.utility.ModelInjector
import kotlinx.android.synthetic.main.activity_detail_community.*

class DetailCommunityActivity : AppCompatActivity() {
    private val fragmentTitleList = mutableListOf("Thread", "Peraturan", "Moderator")
    private val factory = ModelInjector.provideListCommunityViewModeFactory()
    private val userFactory = ModelInjector.provideUserViewModelFactory()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_community)
        val communityId = intent.extras?.get("id") as Int
        val data = communityListModel().getData().map {
            it.find { f -> f.id == communityId }
        }
        initDetailCommunity(data)
        settingTab(data)
        popScreen.setOnClickListener {
            onBackPressed()
        }
        joinBtnOrExit.setOnClickListener {
        }

    }


    private fun communityListModel(): CommunityListModelViewModel {
        return ViewModelProvider(this, factory).get(CommunityListModelViewModel::class.java)
    }

    private fun userViewModel(): UserViewModel {
        return ViewModelProvider(this, userFactory).get(UserViewModel::class.java)
    }

    private fun setCountText(word: String, color: Int, style: Int): Spannable {
        val span =
            SpannableString(word)
        span.setSpan(
            ForegroundColorSpan(getColor(color)),
            0,
            word.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        span.setSpan(
            StyleSpan(style),
            0,
            word.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        return span
    }

    private fun initDetailCommunity(data: LiveData<CommunityListModel?>) {
        data.observe(this, {
            if (it !== null) {
                communityDetailName.text = it.name
                communityDetailDescription.text = it.description
                threadCount.text =
                    setCountText("${it.noThreads}", R.color.kumparan_purple51, Typeface.BOLD)
                threadCount.append(setCountText("\nThread", R.color.black, Typeface.NORMAL))
                memberCount.text =
                    setCountText("${it.noUsers}", R.color.kumparan_purple51, Typeface.BOLD)
                memberCount.append(setCountText("\nMember", R.color.black, Typeface.NORMAL))
            }
        })
    }

    private fun settingTab(communityListLiveData: LiveData<CommunityListModel?>) {
        detailCommunityViewPager.adapter = DetailCommunityViewPager(this, communityListLiveData)
        TabLayoutMediator(detailCommunityTabs, detailCommunityViewPager) { tab, pos ->
            tab.text = fragmentTitleList[pos]
        }.attach()
        detailCommunityViewPager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                detailCommunityViewPager?.adapter?.notifyDataSetChanged()
            }
        })
    }

}