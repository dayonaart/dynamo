package id.kumparan.dynamo.ui.home.rv

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import id.kumparan.dynamo.R
import id.kumparan.dynamo.ReportThreadActivity
import id.kumparan.dynamo.model.MyListThreadModel
import id.kumparan.dynamo.pages.DetailThreadActivity
import id.kumparan.dynamo.utility.Utility
import kotlinx.android.synthetic.main.fragment_home_tab_my_community.*
import kotlinx.android.synthetic.main.rv_thread_item.view.*
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.*
import java.util.concurrent.ThreadLocalRandom


class MyCommunityRVAdapter(
    private val data: List<MyListThreadModel>,
) :
    RecyclerView.Adapter<MyCommunityRVHolder>() {


    override fun onCreateViewHolder(viewGroup: ViewGroup, p1: Int): MyCommunityRVHolder {
        return MyCommunityRVHolder(
            viewGroup.context,
            LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.rv_thread_item, viewGroup, false)
        )
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: MyCommunityRVHolder, position: Int) {
        holder.bindThread(data[position])
    }
}

class MyCommunityRVHolder(private val context: Context, view: View) :
    RecyclerView.ViewHolder(view) {
    private val tvName = view.tvMyCommunityName
    private val tvUsername = view.tvMyUsername
    private val tvDate = view.tvMyCommunityDate
    private val tvDesc = view.tvMyCommunityDesc
    private val imgUrl = view.imgMyCommunityUrl
    private val imgThumbnail = view.imgThumbnail
    private val comments = view.comments
    private val openChat = view.openDetailCommunity
    private val optionBtn = view.optionBtn

    @SuppressLint("SetTextI18n")
    fun bindThread(
        myListThreadModel: MyListThreadModel,
    ) {
        tvName.text = myListThreadModel.communityName
        tvUsername.text = myListThreadModel.username
        tvDate.text = parseDate(myListThreadModel.createdAt)
        tvDesc.text = myListThreadModel.content
        comments.text = "${myListThreadModel.noComments} komentar"
        openChat.setOnClickListener {
            openChat(context, myListThreadModel)
        }

        optionBtn.setOnClickListener {
        Utility.performOptionsMenu(context,optionBtn,myListThreadModel)
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun parseDate(date: String?): String? {
        val sdf = SimpleDateFormat("dd MMMM y")
        return sdf.format(
            Date.from(Instant.parse(date ?: "1990-11-30T18:35:24.00Z"))
        )
    }


    private fun openChat(
        context: Context,
        myListThreadModel: MyListThreadModel?,
    ) {
        val detailThread = Intent(context, DetailThreadActivity::class.java)
        detailThread.putExtra("detailThread", myListThreadModel)
        context.startActivity(detailThread)
    }

    private fun convertBase64ToBitmap(b64: String): Bitmap? {
        val imageAsBytes = Base64.decode(b64.toByteArray(), Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.size)
    }
}

