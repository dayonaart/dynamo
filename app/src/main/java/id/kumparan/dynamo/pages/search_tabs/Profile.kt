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
import id.kumparan.dynamo.model.SearchQueryModel
import id.kumparan.dynamo.model.UserListModel
import kotlinx.android.synthetic.main.rv_profile_list_item.view.*
import kotlinx.android.synthetic.main.search_profile_fragment.*

class Profile : Fragment() {
    private lateinit var searchViewModel: SearchQueryModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.search_profile_fragment, container, false)
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        haveNoResult.visibility=View.VISIBLE
        searchViewModel=activity?.run {
            ViewModelProvider(this).get(SearchQueryModel::class.java)
        } ?: throw Exception("Invalid Activity")
        searchViewModel.getUser().observe(viewLifecycleOwner,{
            val profileAdapter= SearchProfileRVAdapter(it)
            if (it!=null) {
                haveNoResult.visibility = View.GONE
                searchRVProfile.apply {
                    layoutManager = LinearLayoutManager(context)
                    adapter = profileAdapter
                }
            }else{
                searchRVProfile.visibility=View.GONE
                haveNoResult.visibility = View.VISIBLE
            }
        })
    }
}

class SearchProfileRVAdapter(private val data: List<UserListModel>?) :
    RecyclerView.Adapter<SearchProfileRVAdapter.ViewHolder>() {
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val username=view.username
        private val userAvatar=view.userAvatar
        private val karmaPoint=view.karmaPoint

        @SuppressLint("SetTextI18n")
        fun bind(userListModel: UserListModel){
            username.text= userListModel.username
            karmaPoint.text = "${userListModel.karma} Poin Karma"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.rv_profile_list_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
       holder.bind(data?.get(position)!!)
    }

    override fun getItemCount(): Int = data?.size!!
}