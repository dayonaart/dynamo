package id.kumparan.dynamo.pages
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.tabs.TabLayoutMediator
import id.kumparan.dynamo.CreateCommentActivity
import id.kumparan.dynamo.R
import id.kumparan.dynamo.model.MyListThreadModel
import id.kumparan.dynamo.pages.adapter.ThreadTabAdapter
import id.kumparan.dynamo.utility.Utility
import kotlinx.android.synthetic.main.activity_detail_thread.*
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.*

class DetailThreadActivity : AppCompatActivity() {
    private val tabTitle = mutableListOf("Popular", "Terbaru")

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
        toolbar_title.text = detailThread().title
        tvMyCommunityName.text = detailThread().username
        tvMyUsername.text = detailThread().username
        tvMyCommunityTDesc.text = detailThread().content
        tvMyCommunityDate.text = detailThread().createdAt
        comments.text = "${detailThread().noComments}"
        initTextField()
//        if ((detailThread().photo != "" && detailThread().photo != null) && !detailThread().photo?.contains(
//                "data"
//            )!!
//        ) {
//            imgThumbnail.setImageBitmap(convertBase64ToBitmap(detailThread().photo!!))
//        }
        settingTabs()
        optionBtn.setOnClickListener {
            Utility.performOptionsMenu(this,optionBtn,detailThread())
        }
    }

    private fun initTextField(){
        val createCommentActivity = Intent(this, CreateCommentActivity::class.java)
        createCommentField.setOnClickListener {
            createCommentActivity.putExtra("data",detailThread())
            startActivity(createCommentActivity)
        }
    }



    @SuppressLint("SimpleDateFormat")
    private fun parseDate(date: String?): String? {
        val sdf = SimpleDateFormat("dd MMMM y")
        return sdf.format(
            Date.from((Instant.parse(date ?: "1990-11-30T18:35:24.00Z")))
        )
    }

    private fun detailThread(): MyListThreadModel {
        return intent.extras?.get("detailThread") as MyListThreadModel
    }

    private fun settingTabs() {
        val adapter = ThreadTabAdapter(this,detailThread().id)
        detailThreadViewPager.adapter=adapter
        TabLayoutMediator(detailThreadTabs, detailThreadViewPager) { tab, pos ->
            tab.text = tabTitle[pos]
        }.attach()
    }

    private fun convertBase64ToBitmap(b64: String): Bitmap? {
        val imageAsBytes = Base64.decode(b64.toByteArray(), Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.size)
    }
}

