package id.kumparan.dynamo.pages.rv

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import id.kumparan.dynamo.R
import id.kumparan.dynamo.model.ListThreadModel
import id.kumparan.dynamo.pages.data.ThreadChatData
import kotlinx.android.synthetic.main.rv_thread_chat_item.view.*
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.*
import java.util.concurrent.ThreadLocalRandom
import kotlin.random.Random

class DetailThreadChatRVAdapter(private val data: List<ListThreadModel>) :
    RecyclerView.Adapter<DetailThreadChatRVAdapter.ViewHolder>() {
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val random = Random
        private val username = view.userName
        private val message = view.messagesThread
        private val likeCount = view.totalLike
        private val dateChat = view.date
        private val imgThumbnail = view.imgThumbnail
        private val charPool: List<Char> = ('a'..'z') + ('A'..'Z')

        private val totalLike = (0..400).random()
        private val randomString = (1..10)
            .map { _ -> random.nextInt(0, charPool.size) }
            .map(charPool::get)
            .joinToString("");

        @SuppressLint("SetTextI18n")
        fun bindThread(listThreadModel: ListThreadModel) {
            username.text = listThreadModel.username
            message.text = listThreadModel.content
            likeCount.text = "Vote"
            dateChat.text = listThreadModel.createdAt

        }

        @SuppressLint("SimpleDateFormat")
        private fun dateNow(): List<String>? {
            val sdf = SimpleDateFormat("dd MMMM y")
            val date = sdf.format(
                Date.from(between(Instant.parse("1990-11-30T18:35:24.00Z"), Instant.now()))
            )
            return listOf(date, "time")
        }

        private fun between(startInclusive: Instant, endExclusive: Instant): Instant? {
            val startSeconds: Long = startInclusive.epochSecond
            val endSeconds: Long = endExclusive.epochSecond
            val random: Long = ThreadLocalRandom
                .current()
                .nextLong(startSeconds, endSeconds)
            return Instant.ofEpochSecond(random)
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