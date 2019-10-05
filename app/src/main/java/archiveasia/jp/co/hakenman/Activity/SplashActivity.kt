package archiveasia.jp.co.hakenman.Activity

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import archiveasia.jp.co.hakenman.CustomLog
import archiveasia.jp.co.hakenman.Extension.yearMonth
import archiveasia.jp.co.hakenman.Manager.WorksheetManager
import archiveasia.jp.co.hakenman.R
import java.util.*

class SplashActivity : AppCompatActivity() {

    companion object {
        private const val SPLASH_DELAY: Long = 2000 // 2 秒
    }

    private var mDelayHandler: Handler? = null

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
        mDelayHandler!!.postDelayed(mRunnable, SPLASH_DELAY)

        CustomLog.d("スプラッシュ画面")
    }

    override fun onDestroy() {
        if (mDelayHandler != null) {
            mDelayHandler!!.removeCallbacks(mRunnable)
        }

        super.onDestroy()
    }


}
