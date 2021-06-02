package id.kumparan.dynamo.pages.search_tabs

import android.annotation.SuppressLint
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
import kotlinx.android.synthetic.main.rv_thread_item.view.*
import kotlinx.android.synthetic.main.search_thread_fragment.*
import java.text.SimpleDateFormat
import java.util.*

class Thread : Fragment() {
    private lateinit var searchViewModel: SearchQueryModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.search_thread_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        haveNoResult.visibility = View.VISIBLE
        searchViewModel = activity?.run {
            ViewModelProvider(this).get(SearchQueryModel::class.java)
        } ?: throw Exception("Invalid Activity")
        searchViewModel.getThread().observe(viewLifecycleOwner, {
            println("THREAD $it")
            val threadAdapter = SearchThreadRVAdapter(it)
            if (it != null) {
                haveNoResult.visibility = View.GONE
                searchRvThread.apply {
                    layoutManager = LinearLayoutManager(context)
                    adapter = threadAdapter
                }
            } else {
                searchRvThread.visibility = View.GONE
                haveNoResult.visibility = View.VISIBLE
            }
        })

    }

}


class SearchThreadRVAdapter(private val data: List<CommunityListModel>?) :
    RecyclerView.Adapter<SearchCommunityRVHolder>() {

    interface OptionsMenuClickListener {
        fun onOptionsMenuClicked(position: Int)
    }
    override fun onCreateViewHolder(viewGroup: ViewGroup, p1: Int): SearchCommunityRVHolder {
        return SearchCommunityRVHolder(
            LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.rv_thread_item, viewGroup, false)
        )
    }

    override fun getItemCount(): Int = data?.size!!

    override fun onBindViewHolder(holder: SearchCommunityRVHolder, position: Int) {
        holder.bindHero(data?.get(position)!!)
    }
}

class SearchCommunityRVHolder(view: View) : RecyclerView.ViewHolder(view) {
    private val tvName = view.tvMyCommunityName
    private val tvUsername = view.tvMyUsername
    private val tvDate = view.tvMyCommunityDate
    private val tvDesc = view.tvMyCommunityDesc
    private val imgUrl = view.imgMyCommunityUrl
    private val imgThumbnail = view.imgThumbnail
    private val comments = view.comments
    private val totalCommentRand = (0..400).random()
    private val optionBtn=view.optionBtn
    @SuppressLint("SetTextI18n")
    fun bindHero(communityListModel: CommunityListModel) {
        tvName.text = communityListModel.name
        tvUsername.text = (communityListModel.userCreate_Username)
        tvDate.text = communityListModel.createdAt
        tvDesc.text = communityListModel.description
//        Picasso.get()
//            .load(communityListModel.avatar)
//            .into(imgUrl, object : Callback {
//                override fun onSuccess() {
//                    Log.d("PHOTO", "success")
//                }
//
//                override fun onError(e: Exception?) {
//                    Log.d("PHOTO", "error")
//                }
//            })
//        Picasso.get().load(communityListModel.avatar).into(imgThumbnail)
        comments.text = "$totalCommentRand Komentar"
        optionBtn.setOnClickListener{
//            optionsMenuClickListener.onOptionsMenuClicked(position)
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun dateNow(): String? {
        val sdf = SimpleDateFormat("dd MMMM y")
        return sdf.format(Date())
    }
}