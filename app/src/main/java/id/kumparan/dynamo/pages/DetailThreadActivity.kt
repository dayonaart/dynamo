package id.kumparan.dynamo.pages

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.tabs.TabLayout
import id.kumparan.dynamo.R
import id.kumparan.dynamo.pages.adapter.ThreadTabAdapter
import kotlinx.android.synthetic.main.activity_detail_thread.*
import java.io.Serializable

class DetailThreadActivity : AppCompatActivity() {
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_thread)
        setSupportActionBar(toolbar_container)
        collapsing_toolbar.setCollapsedTitleTextColor(
            ContextCompat.getColor(this, R.color.white)
        )
        collapsing_toolbar.setExpandedTitleColor(
            ContextCompat.getColor(this, R.color.kumparan_purple51)
        )

        popScreen.setOnClickListener {
            onBackPressed()
        }
        toolbar_title.text = "TOOLBAR TITLE"
        tvMyCommunityName.text = detailThread().username
        tvMyUsername.text = detailThread().username
        tvMyCommunityTDesc.text = detailThread().desc
        tvMyCommunityDate.text = detailThread().communityDate
        comments.text = "${detailThread().commentCount}"
        if ((detailThread().photo != "" && detailThread().photo != null) && !detailThread().photo?.contains(
                "data"
            )!!
        ) {
            imgThumbnail.setImageBitmap(convertBase64ToBitmap(detailThread().photo!!))
        }
//        Picasso.get().load(detailThread.photo).into(imgThumbnail,object :Callback{
//            override fun onSuccess() {
//                TODO("Not yet implemented")
//            }
//
//            override fun onError(e: Exception?) {
//                imgThumbnail.setImageResource(R.drawable.dynamo_profile)
//            }
//        })
//        Picasso.get().load(detailThread.photo).into(imgMyCommunityUrl, object : Callback {
//            override fun onSuccess() {
//                TODO("Not yet implemented")
//            }
//
//            override fun onError(e: Exception?) {
//                imgMyCommunityUrl.setImageResource(R.drawable.dynamo_profile)
//            }
//
//        })
        settingTabs()
//        val generateThread=(1..100).map {
//            ThreadChatData(null,null,null)
//        }
//        val threadAdapter=ThreadChatRVAdapter(generateThread)
//        rvThreadChat.apply {
//            layoutManager=LinearLayoutManager(context)
//            adapter=threadAdapter
//        }
    }
    private fun detailThread(): DetailThreadViewModel {
        return  intent.extras?.get("detailThread") as DetailThreadViewModel
    }
    private fun settingTabs() {
        val adapter = ThreadTabAdapter(this.supportFragmentManager, 2,detailThread().threadId)
        detailThreadViewPager.adapter = adapter
        detailThreadViewPager.addOnPageChangeListener(
            TabLayout.TabLayoutOnPageChangeListener(
                detailThreadTabs
            )
        )
        detailThreadViewPager.offscreenPageLimit = 2
        detailThreadTabs.setupWithViewPager(detailThreadViewPager)
        detailThreadTabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                detailThreadViewPager.currentItem = tab.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }

    private fun convertBase64ToBitmap(b64: String): Bitmap? {
        val imageAsBytes = Base64.decode(b64.toByteArray(), Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.size)
    }
}

data class DetailThreadViewModel(
    val threadId:Int?,
    val desc: String?,
    val communityName: String?,
    val username: String?,
    val communityDate: String?,
    val photo: String?,
    val commentCount: Int?
) : Serializable