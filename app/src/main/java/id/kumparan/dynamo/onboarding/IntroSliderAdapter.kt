package id.kumparan.dynamo.onboarding

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import id.kumparan.dynamo.R

class IntroSliderAdapter(private val listIntroSlide: List<IntroData>) :
    RecyclerView.Adapter<IntroSliderAdapter.IntroSliderViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IntroSliderViewHolder {
        return IntroSliderViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.intro_slide_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: IntroSliderViewHolder, position: Int) {
        holder.bind(listIntroSlide[position])
    }

    override fun getItemCount(): Int {
        return listIntroSlide.size
    }

    inner class IntroSliderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val title = view.findViewById<TextView>(R.id.titleSLide)
        private val desc = view.findViewById<TextView>(R.id.descriptionSlide)
        private val icon = view.findViewById<ImageView>(R.id.imgSlide)
        fun bind(introData: IntroData) {
            title.text = introData.title
            desc.text = introData.description
            icon.setImageResource(introData.icon)
        }
    }
}