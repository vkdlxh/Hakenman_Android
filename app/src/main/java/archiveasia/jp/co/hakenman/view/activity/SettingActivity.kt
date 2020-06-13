package archiveasia.jp.co.hakenman.view.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import archiveasia.jp.co.hakenman.CustomLog
import archiveasia.jp.co.hakenman.R
import archiveasia.jp.co.hakenman.TimePickerDialog
import archiveasia.jp.co.hakenman.databinding.ActivitySettingBinding
import archiveasia.jp.co.hakenman.manager.PrefsManager
import archiveasia.jp.co.hakenman.manager.ThemeUtil
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.list.listItems
import com.google.android.gms.measurement.module.Analytics
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import java.util.*

class SettingActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingBinding
    private lateinit var prefsManager: PrefsManager
    private lateinit var analytics: FirebaseAnalytics

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        analytics = Firebase.analytics
        analytics.setCurrentScreen(this, "設定画面", null)

        title = getString(R.string.setting_activity_title)

        prefsManager = PrefsManager(this)
        with (binding) {
            emailToEditText.setText(prefsManager.emailTo)
            defaultBeginTimeTextView.text = prefsManager.defaultBeginTime
            defaultEndTimeTextView.text = prefsManager.defaultEndTime
            themeTextView.text = prefsManager.theme
            val packageInfo = packageManager.getPackageInfo(packageName, 0)
            versionTextView.text = packageInfo.versionName

            val selectedButtonId = when (prefsManager.interval) {
                15 -> R.id.button15
                30 -> R.id.button30
                else -> R.id.button1
            }
            intervalRadioGroup.check(selectedButtonId)

            intervalRadioGroup.setOnCheckedChangeListener { _, checkedId ->
                prefsManager.interval = when (checkedId) {
                    R.id.button1 -> 1
                    R.id.button15 -> 15
                    R.id.button30 -> 30
                    else -> 1
                }
                val bundle = Bundle().apply {
                    putInt("interval", prefsManager.interval)
                }
                analytics.logEvent("change_interval", bundle)
            }

            defaultBeginTimeConstraintLayout.setOnClickListener {
                showAddDialog(R.string.set_beginTime_title,
                    defaultBeginTimeTextView,
                    TimePickerDialog.WorkTimeType.BEGIN_TIME)
            }

            defaultEndTimeConstraintLayout.setOnClickListener {
                showAddDialog(R.string.set_endTime_title,
                    defaultEndTimeTextView,
                    TimePickerDialog.WorkTimeType.END_TIME)
            }

            themeLayout.setOnClickListener {
                showThemeDialog()
            }

            tutorialTextView.setOnClickListener {
                analytics.logEvent("tutorial_again", null)
                startActivity(TutorialActivity.createInstance(this@SettingActivity, false))
            }
        }

        CustomLog.d("設定画面")
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == android.R.id.home) {
            prefsManager.emailTo = binding.emailToEditText.text.toString()
            finish()
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        prefsManager.emailTo = binding.emailToEditText.text.toString()
    }

    private fun showAddDialog(titleId: Int, textView: TextView,
                              workTimeType: TimePickerDialog.WorkTimeType) {
        TimePickerDialog(this)
            .title(titleId)
            .show(textView.text.toString(), workTimeType) {
                textView.text = it
                when (workTimeType) {
                    TimePickerDialog.WorkTimeType.BEGIN_TIME -> {
                        prefsManager.defaultBeginTime = it
                    }
                    TimePickerDialog.WorkTimeType.END_TIME -> {
                        prefsManager.defaultEndTime = it
                    }
                    TimePickerDialog.WorkTimeType.BREAK_TIME -> {
                    }
                }
                val eventId = "change_${workTimeType.name.toLowerCase(Locale.getDefault())}"
                val bundle = Bundle().apply {
                    putString("time", it)
                }
                analytics.logEvent(eventId, bundle)
            }
    }

    private fun showThemeDialog() {
        val themeList = listOf(ThemeUtil.LIGHT_MODE, ThemeUtil.DARK_MODE, ThemeUtil.DEFAULT_MODE)
        MaterialDialog(this).show {
            listItems(items = themeList) { _, index, _ ->
                val theme = when (index) {
                    0 -> ThemeUtil.LIGHT_MODE
                    1 -> ThemeUtil.DARK_MODE
                    else -> ThemeUtil.DEFAULT_MODE
                }
                prefsManager.theme = theme
                   ThemeUtil.applyTheme(theme)
                   binding.themeTextView.text = theme
                val bundle = Bundle().apply {
                    putString("theme", theme)
                }
                analytics.logEvent("change_theme", bundle)
           }
       }
    }

    companion object {
        fun createInstance(context: Context) = Intent(context, SettingActivity::class.java)
    }
}
