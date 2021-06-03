package id.kumparan.dynamo.pages.detail_thread_tabs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import id.kumparan.dynamo.R
import id.kumparan.dynamo.model.AllCommentListModel
import id.kumparan.dynamo.model.AllCommentListModelViewModel
import id.kumparan.dynamo.pages.data.ThreadChatData
import id.kumparan.dynamo.pages.rv.ThreadChatRVAdapter
import id.kumparan.dynamo.utility.ModelInjector
import kotlinx.android.synthetic.main.rv_comments_detail_thread.*
import java.time.Instant
import java.util.*

class Comments(private val threadId: Int?) : Fragment() {
    private val allCommentFactory = ModelInjector.provideAllCommentListViewModelFactory()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.rv_comments_detail_thread, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        allComment().getData().observe(viewLifecycleOwner, {
            val commentQuery = it.filter { data ->
                data.threadId == threadId
            }
            commentQuery.sortedByDescending { s->  Date.from(Instant.parse(s.createdAt))}
            val threadAdapter = ThreadChatRVAdapter(requireContext(),commentQuery)
            rvThreadChat.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = threadAdapter
            }
        })

    }

    private fun allComment(): AllCommentListModelViewModel {
        return ViewModelProvider(
            this,
            allCommentFactory
        ).get(AllCommentListModelViewModel::class.java)
    }
}