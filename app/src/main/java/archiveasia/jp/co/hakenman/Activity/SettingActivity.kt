package archiveasia.jp.co.hakenman.Activity

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import archiveasia.jp.co.hakenman.CustomLog
import archiveasia.jp.co.hakenman.Manager.PrefsManager
import archiveasia.jp.co.hakenman.R
import kotlinx.android.synthetic.main.activity_setting.*

class SettingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        title = getString(R.string.setting_activity_title)

        val packageInfo = packageManager.getPackageInfo(packageName, 0)
        version_textView.text = packageInfo.versionName

        if (PrefsManager(this).interval == 15) {
            interval_radio_group.check(R.id.button15)
        } else {
            interval_radio_group.check(R.id.button30)
        }

        interval_radio_group.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.button15 ->
                    PrefsManager(this).interval = 15
                R.id.button30 ->
                    PrefsManager(this).interval = 30
            }

        }

        // メール修正画面に遷移
        go_email_setting_textView.setOnClickListener {
            var intent= Intent(this, EmailSettingActivity::class.java)
            startActivity(intent)
        }

        CustomLog.d("設定画面")
    }

    override fun onResume() {
        super.onResume()

        val prefsManager = PrefsManager(this)

        // Prefマネージャーからメール情報を設定
        if (prefsManager.emailFrom.isNullOrEmpty()) {
            email_from_textView.text = getString(R.string.email_empty_message)
        } else {
            email_from_textView.text = prefsManager.emailFrom
        }

        if (prefsManager.emailTo.isNullOrEmpty()) {
            email_to_textView.text = getString(R.string.email_empty_message)
        } else {
            email_to_textView.text = prefsManager.emailTo
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return if (item?.itemId == android.R.id.home) {
            finish()
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }

}
