package id.kumparan.dynamo.pages.rv

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import id.kumparan.dynamo.R
import id.kumparan.dynamo.pages.data.ThreadChatData
import id.kumparan.dynamo.utility.Utility
import kotlinx.android.synthetic.main.rv_thread_chat_item.view.*
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.*
import java.util.concurrent.ThreadLocalRandom
import kotlin.random.Random

class ThreadChatRVAdapter(private val data: List<ThreadChatData>) :
    RecyclerView.Adapter<ThreadChatRVAdapter.ViewHolder>() {
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val random = Random
        private val username = view.userName
        private val message = view.messagesThread
        private val likeCount = view.totalLike
        private val dateChat = view.date
        private val timeChat = view.time
        private val imgThumbnail = view.imgThumbnail
        private val charPool: List<Char> = ('a'..'z') + ('A'..'Z')
        private val ranChat =
            arrayListOf(
                "quia molestiae reprehenderit quasi aspernatur\naut expedita occaecati aliquam eveniet laudantium\nomnis quibusdam delectus saepe quia accusamus maiores nam est\ncum et ducimus et vero voluptates excepturi deleniti ratione",
                "iste ut laborum aliquid velit facere itaque\nquo ut soluta dicta voluptate\nerror tempore aut et\nsequi reiciendis dignissimos expedita consequuntur libero sed fugiat facilis",
                "consequatur necessitatibus totam sed sit dolorum\nrecusandae quae odio excepturi voluptatum harum voluptas\nquisquam sit ad eveniet delectus\ndoloribus odio qui non labore",
                "voluptatem ut possimus laborum quae ut commodi delectus\nin et consequatur\nin voluptas beatae molestiae\nest rerum laborum et et velit sint ipsum dolorem",
                "omnis dolor autem qui est natus\nautem animi nemo voluptatum aut natus accusantium iure\ninventore sunt ea tenetur commodi suscipit facere architecto consequatur\ndolorem nihil veritatis consequuntur corporis",
                "quibusdam rerum quia nostrum culpa\nculpa est natus impedit quo rem voluptate quos\nrerum culpa aut ut consectetur\nsunt esse laudantium voluptatibus cupiditate rerum",
                "vitae cupiditate excepturi eum veniam laudantium aspernatur blanditiis\naspernatur quia ut assumenda et magni enim magnam\nin voluptate tempora\nnon qui voluptatem reprehenderit porro qui voluptatibus"
            )

        private val totalLike = (0..400).random()
        private val randomString = (1..10)
            .map { _ -> random.nextInt(0, charPool.size) }
            .map(charPool::get)
            .joinToString("");

        @SuppressLint("SetTextI18n")
        fun bindThread(threadChatData: ThreadChatData) {
            username.text = "${threadChatData.username ?: randomString} @Username}"
            message.text = threadChatData.message ?: randomChat()
            likeCount.text = "${threadChatData.likeCount ?: totalLike}"
            dateChat.text = dateNow()!![0]
            timeChat.text = dateNow()!![1]

        }

        private fun randomChat(): String {
            val index = random.nextInt(ranChat.size)
            return ranChat[index]
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