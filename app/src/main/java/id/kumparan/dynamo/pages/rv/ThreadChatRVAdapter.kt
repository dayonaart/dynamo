package id.kumparan.dynamo.pages.rv

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import id.kumparan.dynamo.R
import id.kumparan.dynamo.ReportThreadActivity
import id.kumparan.dynamo.model.AllCommentListModel
import id.kumparan.dynamo.model.MyListThreadModel
import kotlinx.android.synthetic.main.rv_thread_chat_item.view.*
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.*
import kotlin.random.Random

class ThreadChatRVAdapter(private val context: Context, val data: List<AllCommentListModel>) :
    RecyclerView.Adapter<ThreadChatRVAdapter.ViewHolder>() {
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val random = Random
        private val username = view.userName
        private val message = view.messagesThread
        private val totalVote = view.totalVote
        private val dateChat = view.date
        private val imgThumbnail = view.imgThumbnail
        private val charPool: List<Char> = ('a'..'z') + ('A'..'Z')
        private val optionBtn = view.optionBtn


        @SuppressLint("SetTextI18n")
        fun bindThread(context: Context, threadChatData: AllCommentListModel) {
            username.text = threadChatData.username
            message.text = threadChatData.content
            totalVote.text = threadChatData.totalVotes.toString()
            dateChat.text = parseDate(threadChatData.createdAt)
            val commentThread = MyListThreadModel(
                threadChatData.id,
                threadChatData.threadTitle,
                threadChatData.content,
                null,
                threadChatData.userCreateId,
                null,
                threadChatData.createdAt,
                null,
                threadChatData.username,
                threadChatData.threadTitle,
                threadChatData.upvotes,
                threadChatData.downvotes,
                threadChatData.noReplies,
                threadChatData.noReports,
                null
            )
            optionBtn.setOnClickListener {
                performOptionsMenuClick(context, optionBtn, commentThread)
            }
        }


        @SuppressLint("SimpleDateFormat")
        private fun parseDate(date: String?): String? {
            val sdf = SimpleDateFormat("dd MMMM y")
            return sdf.format(
                Date.from(Instant.parse(date ?: "1990-11-30T18:35:24.00Z"))
            )
        }

        private fun performOptionsMenuClick(context: Context, view: View, data: MyListThreadModel) {
            val reportThreadActivity = Intent(context, ReportThreadActivity::class.java)
            reportThreadActivity.putExtra("data", data)
            val popupMenu =
                PopupMenu(context, view)
            popupMenu.inflate(R.menu.option_menu_thread)
            popupMenu.setOnMenuItemClickListener(object : PopupMenu.OnMenuItemClickListener {
                override fun onMenuItemClick(item: MenuItem?): Boolean {
                    when (item?.itemId) {
                        R.id.delete -> {
                            context.startActivity(reportThreadActivity)
                            return true
                        }
                    }
                    return false
                }
            })
            popupMenu.show()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.rv_thread_chat_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindThread(context, data[position])
    }

    override fun getItemCount(): Int = data.size
}