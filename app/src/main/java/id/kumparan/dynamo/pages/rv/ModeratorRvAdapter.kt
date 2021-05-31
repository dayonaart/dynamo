package id.kumparan.dynamo.pages.rv

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import id.kumparan.dynamo.R
import id.kumparan.dynamo.pages.detail_community_tabs.ModeratorResponseModel
import kotlinx.android.synthetic.main.rv_moderator_item.view.*

class ModeratorRvAdapter(private val data: List<ModeratorResponseModel>) :
    RecyclerView.Adapter<ModeratorRvAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val username = view.username
        private val userAvatar = view.userAvatar
        private val karmaPoint = view.karmaPoint
        @SuppressLint("SetTextI18n")
        fun bindThread(moderator: ModeratorResponseModel) {
            if (moderator.username?.trim()?.isEmpty()!!) {
                username.text = "not found"
            } else {
                username.text = moderator.username
            }
            karmaPoint.text = "${moderator.karma} "
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.rv_moderator_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindThread(data[position])

    }

    override fun getItemCount(): Int = data.size
}