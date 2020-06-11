package archiveasia.jp.co.hakenman.activity

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import archiveasia.jp.co.hakenman.CustomLog
import archiveasia.jp.co.hakenman.R
import archiveasia.jp.co.hakenman.TimePickerDialog
import archiveasia.jp.co.hakenman.manager.PrefsManager
import kotlinx.android.synthetic.main.activity_setting.*

class SettingActivity : AppCompatActivity() {

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        title = getString(R.string.setting_activity_title)

        email_to_editText.setText(PrefsManager(this).emailTo)
        default_beginTime_textView.text = PrefsManager(this).defaultBeginTime
        default_endTime_textView.text = PrefsManager(this).defaultEndTime
        val packageInfo = packageManager.getPackageInfo(packageName, 0)
        version_textView.text = packageInfo.versionName

        val selectedButtonId = when (PrefsManager(this).interval) {
            15 -> R.id.button15
            30 -> R.id.button30
            else -> R.id.button1
        }
        interval_radio_group.check(selectedButtonId)

        interval_radio_group.setOnCheckedChangeListener { _, checkedId ->
            PrefsManager(this).interval = when (checkedId) {
                R.id.button1 -> 1
                R.id.button15 -> 15
                R.id.button30 -> 30
                else -> 1
            }
        }

        default_beginTime_ConstraintLayout.setOnClickListener {
            showAddDialog(R.string.set_beginTime_title,
                default_beginTime_textView,
                TimePickerDialog.WorkTimeType.BEGIN_TIME)
        }

        default_endTime_ConstraintLayout.setOnClickListener {
            showAddDialog(R.string.set_endTime_title,
                default_endTime_textView,
                TimePickerDialog.WorkTimeType.END_TIME)
        }

        CustomLog.d("設定画面")
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return if (item?.itemId == android.R.id.home) {
            PrefsManager(this).emailTo = email_to_editText.text.toString()
            finish()
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        PrefsManager(this).emailTo = email_to_editText.text.toString()
    }

    private fun showAddDialog(titleId: Int, textView: TextView,
                              workTimeType: TimePickerDialog.WorkTimeType) {
        TimePickerDialog(this)
            .title(titleId)
            .show(textView.text.toString(), workTimeType) {
                textView.text = it
                when (workTimeType) {
                    TimePickerDialog.WorkTimeType.BEGIN_TIME ->
                        PrefsManager(this).defaultBeginTime = it
                    TimePickerDialog.WorkTimeType.END_TIME ->
                        PrefsManager(this).defaultEndTime = it
                    TimePickerDialog.WorkTimeType.BREAK_TIME ->
                        CustomLog.d("何もしない")
                }
            }
    }
}
