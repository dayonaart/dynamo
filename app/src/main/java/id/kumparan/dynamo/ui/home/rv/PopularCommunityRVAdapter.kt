package id.kumparan.dynamo.ui.home.rv

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import id.kumparan.dynamo.R
import id.kumparan.dynamo.model.ListThreadModel
import id.kumparan.dynamo.model.MyListThreadModel
import id.kumparan.dynamo.pages.DetailThreadActivity
import id.kumparan.dynamo.utility.Utility
import kotlinx.android.synthetic.main.rv_thread_item.view.*
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.*
import java.util.concurrent.ThreadLocalRandom


class PopularCommunityRVAdapter(
    private val data: List<ListThreadModel>,
    private val userId: Int,
    private val upVoteClickListener: (myListThreadModel: MyListThreadModel) -> Unit,
    private val downVoteClickListener:(myListThreadModel: MyListThreadModel)->Unit,

    ) :
    RecyclerView.Adapter<PopularCommunityRVHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, p1: Int): PopularCommunityRVHolder {
        return PopularCommunityRVHolder(
            viewGroup.context,
            LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.rv_thread_item, viewGroup, false)
        )
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: PopularCommunityRVHolder, position: Int) {
        holder.bindThread(data[position],userId, upVoteClickListener,downVoteClickListener)
    }
}

class PopularCommunityRVHolder(private val context: Context, view: View) :
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
    private val upVoteBtn = view.voteUpBtn
    private val downVoteBtn = view.voteDownBtn

    @SuppressLint("SetTextI18n")
    fun bindThread(
        listThreadModel: ListThreadModel,
        userId: Int,
        upVoteClickListener: (myListThreadModel: MyListThreadModel) -> Unit,
        downVoteClickListener: (myListThreadModel: MyListThreadModel) -> Unit,
        ) {
        tvName.text = listThreadModel.communityName
        tvUsername.text = listThreadModel.username
        tvDate.text = dateNow(listThreadModel.createdAt)
        tvDesc.text = listThreadModel.content
        comments.text = "${listThreadModel.noComments} Komentar"
        val isVoted = listThreadModel.voteUserList?.find { it.userCreateId == userId }
        val detailThreadView = MyListThreadModel(
            listThreadModel.id,
            listThreadModel.title,
            listThreadModel.content,
            listThreadModel.id,
            listThreadModel.userCreateId,
            listThreadModel.userUpdateId,
            listThreadModel.createdAt,
            listThreadModel.updatedAt,
            listThreadModel.username,
            listThreadModel.communityName,
            listThreadModel.upVote,
            listThreadModel.downVote,
            listThreadModel.noComments,
            listThreadModel.noReports,
            listThreadModel.score,
            null
        )
        if (isVoted != null) {
            upVoteBtn.setImageResource(R.drawable.ic_green_vote_arrow_up)
        }else{
            upVoteBtn.rotation= 180F
            upVoteBtn.setImageResource(R.drawable.ic_arrow)
        }
        upVoteBtn.setOnClickListener {
            upVoteClickListener(detailThreadView)
        }
        downVoteBtn.setOnClickListener {
            downVoteClickListener(detailThreadView)
        }

        openChat.setOnClickListener {
            openChat(context, detailThreadView)
        }
        optionBtn.setOnClickListener {
            Utility.performOptionsMenu(context, optionBtn, detailThreadView)
        }

    }

    @SuppressLint("SimpleDateFormat")
    private fun dateNow(date: String?): String? {
        val sdf = SimpleDateFormat("dd MMMM y")
        return sdf.format(
            Date.from(between(Instant.parse(date ?: "1990-11-30T18:35:24.00Z"), Instant.now()))
        )
    }

    private fun between(startInclusive: Instant, endExclusive: Instant): Instant? {
        val startSeconds: Long = startInclusive.epochSecond
        val endSeconds: Long = endExclusive.epochSecond
        val random: Long = ThreadLocalRandom
            .current()
            .nextLong(startSeconds, endSeconds)
        return Instant.ofEpochSecond(random)
    }

    private fun openChat(
        context: Context,
        myListThreadModel: MyListThreadModel,
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