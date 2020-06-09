package archiveasia.jp.co.hakenman.view.activity

import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import archiveasia.jp.co.hakenman.CustomLog
import archiveasia.jp.co.hakenman.databinding.ActivitySplashBinding
import archiveasia.jp.co.hakenman.extension.yearMonth
import archiveasia.jp.co.hakenman.manager.PrefsManager
import archiveasia.jp.co.hakenman.manager.ThemeUtil
import archiveasia.jp.co.hakenman.manager.WorksheetManager
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import java.util.Date

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private lateinit var analytics: FirebaseAnalytics

    private var mDelayHandler: Handler = Handler()
    private val mRunnable: Runnable = Runnable {
        if (!isFinishing) {
            val prefsManager = PrefsManager(this)
            val intent = if (prefsManager.isNeedTutorial) {
                TutorialActivity.createInstance(this, true)
            } else {
                WorksheetListActivity.createInstance(this)
            }
            startActivity(intent)
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        analytics = Firebase.analytics
        analytics.setCurrentScreen(this, "スプラッシュ画面", null)

        WorksheetManager.loadLocalWorksheet()
        val currentYearMonth = Date().yearMonth()
        if (!WorksheetManager.isAlreadyExistWorksheet(currentYearMonth)) {
            val worksheet = WorksheetManager.createWorksheet(currentYearMonth)
            WorksheetManager.addWorksheetToJsonFile(worksheet)
        }
        val theme = PrefsManager(this).theme
        ThemeUtil.applyTheme(theme)

        mDelayHandler = Handler()
        mDelayHandler.postDelayed(mRunnable, SPLASH_DELAY)

        CustomLog.d("スプラッシュ画面")
    }

    override fun onDestroy() {
        mDelayHandler.removeCallbacks(mRunnable)
        super.onDestroy()
    }

    companion object {
        private const val SPLASH_DELAY: Long = 2000 // 2 秒
    }
}
