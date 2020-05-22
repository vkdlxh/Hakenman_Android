package archiveasia.jp.co.hakenman.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import archiveasia.jp.co.hakenman.CustomLog
import archiveasia.jp.co.hakenman.R
import archiveasia.jp.co.hakenman.extension.yearMonth
import archiveasia.jp.co.hakenman.manager.WorksheetManager
import java.util.Date

class SplashActivity : AppCompatActivity() {

    companion object {
        private const val SPLASH_DELAY: Long = 2000 // 2 秒
    }

    private var mDelayHandler: Handler = Handler()

    private val mRunnable: Runnable = Runnable {
        if (!isFinishing) {
            val intent = Intent(applicationContext, WorksheetListActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        WorksheetManager.loadLocalWorksheet()
        val currentYearMonth = Date().yearMonth()
        if (!WorksheetManager.isAlreadyExistWorksheet(currentYearMonth)) {
            val worksheet = WorksheetManager.createWorksheet(currentYearMonth)
            WorksheetManager.addWorksheetToJsonFile(worksheet)
        }

        mDelayHandler = Handler()
        mDelayHandler.postDelayed(mRunnable, SPLASH_DELAY)

        CustomLog.d("スプラッシュ画面")
    }

    override fun onDestroy() {
        mDelayHandler.removeCallbacks(mRunnable)
        super.onDestroy()
    }
}
