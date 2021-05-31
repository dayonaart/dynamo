package id.kumparan.dynamo
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.viewpager2.widget.ViewPager2
import id.kumparan.dynamo.onboarding.IntroData
import id.kumparan.dynamo.onboarding.IntroSliderAdapter
import kotlinx.android.synthetic.main.activity_intro.*

class IntroActivity : AppCompatActivity() {
    private val intro = IntroSliderAdapter(
        listOf(
            IntroData(
                "Menaungi Berbagai Komunitas",
                "Gabung dengan berbagai komunitas dari segala bidang minat untuk saling berbagi informasi, bertanya maupun berdiskusi.",
                R.drawable.community_tree
            ),
            IntroData(
                "Thread Sebagai Sarana Informasi",
                "Dapatkan berbagai informasi komunitas dan jangan lupa untuk mendukung thread yang menurutmu bagus dengan cara memberikan upvote!",
                R.drawable.thread_information
            )
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_intro)
        viewPagerBoard.adapter = intro
        setupIndicator()
        setupCurrentIndicator(0)
        val startingActivity= Intent(this, StartingActivity::class.java)
        skipBtn.setOnClickListener{
            startActivity(startingActivity)
            finish()
        }
        viewPagerBoard.registerOnPageChangeCallback(object :ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                setupCurrentIndicator(position)
                if((position+1)==intro.itemCount){
                    skipBtn.visibility=View.VISIBLE
                }else{
                    skipBtn.visibility=View.GONE
                }
            }
        })
    }

    private fun setupIndicator() {
        val indicators = arrayOfNulls<ImageView>(intro.itemCount)
        val layoutParams: LinearLayout.LayoutParams =
            LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
        layoutParams.setMargins(10, 0, 10, 0)
        for (i in indicators.indices) {
            indicators[i] = ImageView(applicationContext)
            indicators[i].apply {
                this?.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.indicator_inactive
                    )
                )
                this?.layoutParams=layoutParams
            }
            indicatorScroll.addView(indicators[i])
        }
    }

   private fun setupCurrentIndicator(index:Int){
        val count= indicatorScroll.childCount
        for (i in 0 until count){
            val imageView = indicatorScroll[i] as ImageView
            if(i==index){
                imageView.setImageDrawable( ContextCompat.getDrawable(
                    applicationContext,
                    R.drawable.indicator_active
                ))
            }else{
                imageView.setImageDrawable( ContextCompat.getDrawable(
                    applicationContext,
                    R.drawable.indicator_inactive
                ))
            }
        }
    }
}