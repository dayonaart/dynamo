package id.kumparan.dynamo.pages.search_tabs

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import id.kumparan.dynamo.R
import id.kumparan.dynamo.model.CommunityListModel
import id.kumparan.dynamo.model.SearchQueryModel
import id.kumparan.dynamo.pages.DetailCommunityActivity
import kotlinx.android.synthetic.main.rv_community_list_item.view.*
import kotlinx.android.synthetic.main.search_communities_fragment.*

class Communities : Fragment() {
    private lateinit var searchViewModel: SearchQueryModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.search_communities_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        haveNoResult.visibility=View.VISIBLE
        searchViewModel = activity?.run {
            ViewModelProvider(this).get(SearchQueryModel::class.java)
        } ?: throw Exception("Invalid Activity")
        searchViewModel.getCommunity().observe(viewLifecycleOwner, {
            val threadAdapter = CommunityRVAdapter(it)
            if (it!=null) {
                haveNoResult.visibility = View.GONE
                searchRvCommunity.apply {
                    layoutManager = LinearLayoutManager(context)
                    adapter = threadAdapter
                }
            }else{
                searchRvCommunity.visibility=View.GONE
                haveNoResult.visibility = View.VISIBLE
            }
        })
    }
}

class CommunityRVAdapter(private val data: List<CommunityListModel>?) :
    RecyclerView.Adapter<CommunityRVAdapter.ViewHolder>() {
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val communityName = view.communityName
        private val communityThread = view.threadCount
        private val communityImg = view.communityImg
        private val communityMember = view.memberCount
        private val onClick=view.openDetailCommunity
        private val detailCommunity = Intent(view.context, DetailCommunityActivity::class.java)
        @SuppressLint("SetTextI18n")
        fun bind(communityListModel: CommunityListModel) {
            detailCommunity.putExtra("id", communityListModel.id)
            communityName.text = communityListModel.name
            communityThread.text = "${communityListModel.noThreads} Thread · "
            communityMember.text = "${communityListModel.noUsers} Member"
        }
        init {
            onClick.setOnClickListener{
                view.context.startActivity(detailCommunity)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.rv_community_list_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data?.get(position)!!)
    }

    override fun getItemCount(): Int = data?.size!!
}