package id.kumparan.dynamo.pages.detail_community_tabs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import id.kumparan.dynamo.R
import id.kumparan.dynamo.model.CommunityListModel
import id.kumparan.dynamo.model.ListThreadModelViewModel
import id.kumparan.dynamo.pages.data.ThreadChatData
import id.kumparan.dynamo.pages.rv.DetailThreadChatRVAdapter
import id.kumparan.dynamo.pages.rv.ThreadChatRVAdapter
import id.kumparan.dynamo.utility.ModelInjector
import kotlinx.android.synthetic.main.rv_comments_detail_thread.*

class Threads(private val communityListLiveData: LiveData<CommunityListModel?>):Fragment() {
    private val allCommentFactory = ModelInjector.provideAllCommentListViewModelFactory()
    private val listAllThreadFactory = ModelInjector.provideListThreadViewModelFactory()

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
            listThreadModel().getData().observe(viewLifecycleOwner,{thread->
                val threadQuery=thread.filter { f->f.communityId==it?.id }
                val threadAdapter= DetailThreadChatRVAdapter(requireContext(), threadQuery)
                if (threadQuery.isEmpty()){
                    haveNoThread.visibility=View.VISIBLE
                }else{
                    haveNoThread.visibility=View.GONE
                    rvThreadChat.apply {
                        layoutManager= LinearLayoutManager(context)
                        adapter=threadAdapter
                    }
                }
            })

        })

    }
    private fun listThreadModel(): ListThreadModelViewModel {
        return ViewModelProvider(this, listAllThreadFactory).get(ListThreadModelViewModel::class.java)
    }
}