package id.kumparan.dynamo.pages.detail_thread_tabs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import id.kumparan.dynamo.R
import id.kumparan.dynamo.pages.data.ThreadChatData
import id.kumparan.dynamo.pages.rv.ThreadChatRVAdapter
import kotlinx.android.synthetic.main.rv_comments_detail_thread.*

class Comments:Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.rv_comments_detail_thread,container,false)
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val generateThread=(1..100).map {
            ThreadChatData(null,null,null)
        }
        val threadAdapter=ThreadChatRVAdapter(generateThread)
        rvThreadChat.apply {
            layoutManager=LinearLayoutManager(context)
            adapter=threadAdapter
        }
    }
}