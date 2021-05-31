package id.kumparan.dynamo.ui.community.rv

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import id.kumparan.dynamo.R
import id.kumparan.dynamo.ui.community.tabs.ParentModel
import kotlinx.android.synthetic.main.rv_tab_popular_parent_popular.view.*

class PopularParentRvAdapter(private val parents: List<ParentModel>,private val onJoin:(id:Int) -> Unit) :
    RecyclerView.Adapter<PopularParentRvAdapter.ViewHolder>() {
    private val viewPool = RecyclerView.RecycledViewPool()
    override fun onCreateViewHolder(
        parent: ViewGroup,
        i: Int
    ): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.rv_tab_popular_parent_popular, parent, false)
        return ViewHolder(v,parent.context)
    }

    override fun getItemCount(): Int {
        return parents.size
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
      holder.bindParent(parents[position])
    }

    inner class ViewHolder(view: View,context: Context) :
        RecyclerView.ViewHolder(view) {
        private val recyclerView = view.rv_child
        private val categoryTitle = view.categoryTitle
        private val communityTotal = view.communityTotal
        private val childLayoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        @SuppressLint("SetTextI18n")
        fun bindParent( parentModel: ParentModel) {
            categoryTitle.text=parentModel.title
            communityTotal.text="${parentModel.children.size} Komunitas"
            childLayoutManager.initialPrefetchItemCount=4
            recyclerView.apply {
                layoutManager = childLayoutManager
                adapter = PopularCardRVAdapter(parentModel.children,onJoin)
                setRecycledViewPool(viewPool)
            }
        }

    }
}