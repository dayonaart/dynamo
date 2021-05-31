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
import id.kumparan.dynamo.pages.DetailThreadViewModel
import kotlinx.android.synthetic.main.rv_thread_item.view.*
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.*
import java.util.concurrent.ThreadLocalRandom


class MyCommunityRVAdapter(private val data: List<MyListThreadModel>) :
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

    @SuppressLint("SetTextI18n")
    fun bindThread(myListThreadModel: MyListThreadModel) {
        tvName.text = myListThreadModel.communityName
        tvUsername.text = myListThreadModel.username
        tvDate.text = dateNow(myListThreadModel.createdAt)
        tvDesc.text = myListThreadModel.content
//        if ((myListThreadModel.userPhoto != "" && myListThreadModel.userPhoto != null) && !myListThreadModel.userPhoto.contains(
//                "data"
//            )
//        ) {
//            imgUrl.setImageBitmap(convertBase64ToBitmap(myListThreadModel.userPhoto))
//            imgThumbnail.setImageBitmap(convertBase64ToBitmap(myListThreadModel.userPhoto))
//        }
//        Picasso.get()
//            .load(userListData.photo).placeholder(R.drawable.dynamo_profile)
//            .into(imgUrl, object : Callback {
//                override fun onSuccess() {
//                    Log.d("PHOTO", "success")
//                }
//
//                override fun onError(e: Exception?) {
//                    imgUrl.setImageResource(R.drawable.dynamo_profile)
//                }
//            })
//        Picasso.get().load(userListData.photo).placeholder(R.drawable.dynamo_profile)
//            .into(imgThumbnail, object : Callback {
//                override fun onSuccess() {
//                    TODO("Not yet implemented")
//                }
//
//                override fun onError(e: java.lang.Exception?) {
//                    imgThumbnail.setImageResource(R.drawable.dynamo_profile)
//                }
//
//            })

        comments.text ="${myListThreadModel.noComments} komentar"
        openChat.setOnClickListener {
            val detailThreadView = DetailThreadViewModel(
                myListThreadModel.content,
                myListThreadModel.communityName,
                myListThreadModel.username,
                myListThreadModel.createdAt,
                null,
//                myListThreadModel.userPhoto,
                myListThreadModel.noComments

            )
            openChat(context, detailThreadView, myListThreadModel.communityId)
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
        detailThreadViewModel: DetailThreadViewModel,
        communityId: Int?
    ) {
        val detailThread = Intent(context, DetailThreadActivity::class.java)
        detailThread.putExtra("detailThread", detailThreadViewModel)
        context.startActivity(detailThread)
//        val detailCommunity = Intent(context, DetailCommunityActivity::class.java)
//        detailCommunity.putExtra("id",communityId)
//        context.startActivity(detailCommunity)
    }

    private fun convertBase64ToBitmap(b64: String): Bitmap? {
        val imageAsBytes = Base64.decode(b64.toByteArray(), Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.size)
    }
}