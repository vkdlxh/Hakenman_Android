package archiveasia.jp.co.hakenman.view.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import archiveasia.jp.co.hakenman.R
import archiveasia.jp.co.hakenman.databinding.ActivityTutorialBinding
import archiveasia.jp.co.hakenman.manager.PrefsManager
import archiveasia.jp.co.hakenman.model.Step
import archiveasia.jp.co.hakenman.view.adapter.TutorialViewPagerAdapter

class TutorialActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityTutorialBinding
    private var isFirstTutorial = false

    val stepList = listOf(
        Step("一覧確認", "", R.drawable.test),
        Step("簡単登録", "", R.drawable.ic_more_vert_black_24dp),
        Step("CSV共有可能", "", R.drawable.ic_chevron_right_lightgray_24dp),
        Step("もっと楽に", "", R.drawable.ic_mail_outline_white_24dp)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTutorialBinding.inflate(layoutInflater)
        setContentView(binding.root)

        isFirstTutorial = intent.getBooleanExtra(EXTRA_FIRST_TUTORIAL, false)

        binding.viewPager.adapter = TutorialViewPagerAdapter(stepList)
        binding.viewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                notifyIndicator(position)
                if (position == stepList.size -1) {
                    setAlphaAnimation(binding.skipTextView, false)
                    setAlphaAnimation(binding.doneTextView, true)
                } else {
                    setAlphaAnimation(binding.skipTextView, true)
                    setAlphaAnimation(binding.doneTextView, false)
                }
            }
        })
        binding.skipTextView.setOnClickListener(this)
        binding.doneTextView.setOnClickListener(this)
        notifyIndicator(0)
    }

    override fun finish() {
        if (isFirstTutorial) {
            startActivity(WorksheetListActivity.createInstance(this))
        }
        PrefsManager(this).isNeedTutorial = false
        super.finish()
    }

    override fun onClick(view: View) {
        finish()
    }

    private fun notifyIndicator(position: Int) {
        with (binding) {
            if (indicator.childCount > 0) {
                indicator.removeAllViews()
            }

            for (i in stepList.indices) {
                val imageView = ImageView(this@TutorialActivity).apply {
                    setPadding(8, 8, 8, 8)
                    val resId = if (i == position) {
                        R.drawable.indicator_enabled
                    } else {
                        R.drawable.indicator_disabled
                    }
                    setImageResource(resId)
                    setOnClickListener {
                        viewPager.currentItem = i
                    }
                }
                indicator.addView(imageView)
            }

        }
    }

    private fun setAlphaAnimation(view: View, isFadeIn: Boolean) {
        if (view.isShown != isFadeIn) {
            if (isFadeIn) {
                view.visibility = View.VISIBLE
            }
            val fromAlpha = if (isFadeIn) 0.0f else 1.0f
            val toAlpha = if (isFadeIn) 1.0f else 0.0f
            val animation = AlphaAnimation(fromAlpha, toAlpha).apply {
                setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationRepeat(p0: Animation?) {}

                    override fun onAnimationEnd(p0: Animation?) {
                        if (!isFadeIn) {
                            view.visibility = View.GONE
                        }
                    }

                    override fun onAnimationStart(p0: Animation?) {}
                })
                duration = 500
            }
            view.animation = animation
        }
    }

    companion object {
        private const val EXTRA_FIRST_TUTORIAL = "EXTRA_FIRST_TUTORIAL"

        fun createInstance(context: Context, isFirst: Boolean) =
            Intent(context, TutorialActivity::class.java).apply {
                putExtra(EXTRA_FIRST_TUTORIAL, isFirst)
            }
    }
}