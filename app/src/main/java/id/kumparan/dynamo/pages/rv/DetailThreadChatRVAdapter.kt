package id.kumparan.dynamo.pages.rv

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import id.kumparan.dynamo.R
import id.kumparan.dynamo.model.ListThreadModel
import id.kumparan.dynamo.model.MyListThreadModel
import id.kumparan.dynamo.pages.DetailThreadActivity
import id.kumparan.dynamo.utility.Utility
import kotlinx.android.synthetic.main.rv_thread_chat_item.view.*
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.*
import kotlin.random.Random

class DetailThreadChatRVAdapter(private val context: Context, private val data: List<ListThreadModel>,private val isModerator:Boolean) :
    RecyclerView.Adapter<DetailThreadChatRVAdapter.ViewHolder>() {
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val random = Random
        private val username = view.userName
        private val message = view.messagesThread
        private val totalVote = view.totalVote
        private val dateChat = view.date
        private val noComment = view.noComment
        private val optionBtn=view.optionBtn
        private val imgThumbnail = view.imgThumbnail
        private val charPool: List<Char> = ('a'..'z') + ('A'..'Z')
        private val openChat=view.openChat
        private val totalLike = (0..400).random()
        private val randomString = (1..10)
            .map { _ -> random.nextInt(0, charPool.size) }
            .map(charPool::get)
            .joinToString("");

        @SuppressLint("SetTextI18n")
        fun bindThread(listThreadModel: ListThreadModel) {
            username.text = listThreadModel.username
            message.text = listThreadModel.content
            totalVote.text = "${listThreadModel.score}"
            dateChat.text = parseDate(listThreadModel.createdAt)
            noComment.text = "${listThreadModel.noComments}"
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
            optionBtn.setOnClickListener {
                if (isModerator){
                    Utility.performOptionsMenu2(context,optionBtn,detailThreadView)
                }else{
                    Utility.performOptionsMenu(context,optionBtn,detailThreadView)
                }
            }
            openChat.setOnClickListener {
                openChat(context,detailThreadView)
            }
        }

        @SuppressLint("SimpleDateFormat")
        private fun parseDate(date: String?): String? {
            val sdf = SimpleDateFormat("dd MMMM y")
            return sdf.format(
                Date.from(Instant.parse(date.let { "1990-11-30T18:35:24.00Z" }))
            )
        }

        private fun openChat(
            context: Context,
            myListThreadModel: MyListThreadModel,
        ) {
            val detailThread = Intent(context, DetailThreadActivity::class.java)
            detailThread.putExtra("detailThread", myListThreadModel)
            context.startActivity(detailThread)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.rv_thread_chat_item, parent, false)
        )
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindThread(data[position])
    }

    override fun getItemCount(): Int = data.size
}