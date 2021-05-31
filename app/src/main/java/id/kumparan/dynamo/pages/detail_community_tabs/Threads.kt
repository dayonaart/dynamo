package id.kumparan.dynamo.pages.detail_community_tabs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.LinearLayoutManager
import id.kumparan.dynamo.R
import id.kumparan.dynamo.model.CommunityListModel
import id.kumparan.dynamo.pages.data.ThreadChatData
import id.kumparan.dynamo.pages.rv.ThreadChatRVAdapter
import kotlinx.android.synthetic.main.rv_comments_detail_thread.*

class Threads(private val communityListLiveData: LiveData<CommunityListModel?>):Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.rv_comments_detail_thread,container,false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        communityListLiveData.observe(viewLifecycleOwner,{
            val generateThread=(1..it?.noThreads!!).map {
                ThreadChatData(null,null,null)
            }
            val threadAdapter= ThreadChatRVAdapter(generateThread)
            if (generateThread.isEmpty()){
                haveNoThread.visibility=View.VISIBLE
            }else{
                haveNoThread.visibility=View.GONE
                rvThreadChat.apply {
                    layoutManager= LinearLayoutManager(context)
                    adapter=threadAdapter
                }
            }

        })

    }
}