package id.kumparan.dynamo.ui.community.rv
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import id.kumparan.dynamo.R
import id.kumparan.dynamo.api.Api
import id.kumparan.dynamo.model.UserViewModel
import id.kumparan.dynamo.ui.community.data.PopularData
import id.kumparan.dynamo.utility.ModelInjector
import kotlinx.android.synthetic.main.rv_popular_card_item.view.*
import java.io.Serializable

class PopularCardRVAdapter(private val data: List<PopularData>,private val onJoin:(id:Int) -> Unit) :
    RecyclerView.Adapter<PopularCardRVAdapter.ViewHolder>() {
    inner class ViewHolder(view : View) : RecyclerView.ViewHolder(view){
        private val imgThumbnail = view.imgThumbnail
        private val title = view.title
        private val tableCount = view.tableCount
        private val peopleCount = view.peopleCount
        private val joinBtn = view.joinBtn
        private val totalPeopleRandom = (0..400).random()
        private val totalTableRandom= (0..400).random()
        fun bindPopular(popularData: PopularData){
//            imgThumbnail.setImageResource(R.drawable.community_tree)
            title.text=popularData.title
            tableCount.text="${popularData.threadCount?:totalTableRandom}"
            peopleCount.text="${popularData.memberCount?:totalPeopleRandom}"
            joinBtn.setOnClickListener{
                onJoin(popularData.id!!)
            }
        }

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v =  LayoutInflater.from(parent.context)
            .inflate(R.layout.rv_popular_card_item,parent,false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindPopular(data[position])
    }
    override fun getItemCount(): Int = data.size

}

