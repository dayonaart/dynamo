package id.kumparan.dynamo.ui.community.rv

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import id.kumparan.dynamo.R
import id.kumparan.dynamo.model.MyCommunityModel
import id.kumparan.dynamo.pages.DetailCommunityActivity
import kotlinx.android.synthetic.main.rv_tab_my_community_item.view.*
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.*
import java.util.concurrent.ThreadLocalRandom

class CommunityRVAdapter(private val data: List<MyCommunityModel>) :
    RecyclerView.Adapter<CommunityRVAdapter.ViewHolder>() {
    inner class ViewHolder(private val context: Context, view: View) :
        RecyclerView.ViewHolder(view) {
        private val charPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
        private val communityImg = view.communityImg
        private val communityName = view.communityName
        private val communityThread = view.threadCount
        private val communityMember = view.memberCount
        private val openDetailCommunity = view.openDetailCommunity
        private val randomString = (1..5)
            .map { _ -> kotlin.random.Random.nextInt(0, charPool.size) }
            .map(charPool::get)
            .joinToString("");

        @SuppressLint("SetTextI18n")
        fun bindCommunity(myCommunityModel: MyCommunityModel) {
            communityName.text = "${myCommunityModel.communityName} Moderator : ${myCommunityModel.isModerator}"
            communityThread.text = "Thread not found · "
            communityMember.text = "Member not found"
            if ((myCommunityModel.userPhoto != "" && myCommunityModel.userPhoto != null) && !myCommunityModel.userPhoto.contains(
                    "data"
                )
            ) {
                communityImg.setImageBitmap(convertBase64ToBitmap(myCommunityModel.userPhoto))
            }

            openDetailCommunity.setOnClickListener {
                openDetailCommunity(context, myCommunityModel.communityId)
            }
        }


        private fun convertBase64ToBitmap(b64: String): Bitmap? {
            val imageAsBytes = Base64.decode(b64.toByteArray(), Base64.DEFAULT)
            return BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.size)
        }

        private fun openDetailCommunity(context: Context, communityId: Int?) {
            val detailCommunity = Intent(context, DetailCommunityActivity::class.java)
            detailCommunity.putExtra("id", communityId)
            context.startActivity(detailCommunity)

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            parent.context,
            LayoutInflater.from(parent.context)
                .inflate(R.layout.rv_tab_my_community_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindCommunity(data[position])
    }

    override fun getItemCount(): Int = data.size

}
