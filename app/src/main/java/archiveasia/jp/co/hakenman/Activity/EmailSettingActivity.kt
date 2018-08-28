package archiveasia.jp.co.hakenman.Activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import archiveasia.jp.co.hakenman.Manager.PrefsManager
import archiveasia.jp.co.hakenman.R
import kotlinx.android.synthetic.main.activity_email_setting.*

class EmailSettingActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_email_setting)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val prefsManager = PrefsManager(this)
        email_from_editText.setText(prefsManager.emailFrom)
        email_to_editText.setText(prefsManager.emailTo)

        title = getString(R.string.email_setting_activity_title)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            android.R.id.home -> {
                // TODO : TextViewに入力された値をPrefsに保存
                val prefsManager = PrefsManager(this)
                prefsManager.emailFrom = email_from_editText.text.toString()
                prefsManager.emailTo = email_to_editText.text.toString()
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
