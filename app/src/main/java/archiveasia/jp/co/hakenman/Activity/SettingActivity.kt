package archiveasia.jp.co.hakenman.Activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import archiveasia.jp.co.hakenman.Manager.PrefsManager
import archiveasia.jp.co.hakenman.R
import kotlinx.android.synthetic.main.activity_setting.*

class SettingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

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
    }
}
