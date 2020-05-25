package archiveasia.jp.co.hakenman.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import archiveasia.jp.co.hakenman.CustomLog
import archiveasia.jp.co.hakenman.R
import archiveasia.jp.co.hakenman.TimePickerDialog
import archiveasia.jp.co.hakenman.extension.day
import archiveasia.jp.co.hakenman.extension.hourMinute
import archiveasia.jp.co.hakenman.extension.hourMinuteToDate
import archiveasia.jp.co.hakenman.extension.month
import archiveasia.jp.co.hakenman.extension.year
import archiveasia.jp.co.hakenman.manager.PrefsManager
import archiveasia.jp.co.hakenman.manager.WorksheetManager
import archiveasia.jp.co.hakenman.model.DetailWork
import archiveasia.jp.co.hakenman.model.Worksheet
import kotlinx.android.synthetic.main.activity_daily_work.*

class DailyWorkActivity : AppCompatActivity() {

    private var index: Int = -1
    private lateinit var worksheet: Worksheet
    private lateinit var detailWork: DetailWork

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_daily_work)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        index = intent.getIntExtra(INTENT_DETAILWORK_INDEX, index)
        worksheet = intent.getParcelableExtra(INTENT_DETAILWORK_VALUE)
        detailWork = worksheet.detailWorkList[index]
        setDetailWork()

        worksheet_form_view.visibility = if (detailWork.workFlag) View.VISIBLE else View.INVISIBLE
        isWork_switch.isChecked = detailWork.workFlag

        isWork_switch.setOnCheckedChangeListener { _, isChecked ->
            worksheet_form_view.visibility = if (isChecked) View.VISIBLE else View.INVISIBLE
            detailWork.workFlag = isChecked
        }

        title = getString(R.string.day_work_activity_title)
                .format(detailWork.workDate.year(), detailWork.workDate.month(), detailWork.workDate.day())
        CustomLog.d("1日詳細勤務表画面")
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.add_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.action_add -> {
                saveWorksheet()
                finish()
                return true
            }
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun saveWorksheet() {
        val beginTime = with (day_start_time_textView.text) {
            if (isNotEmpty()) toString().hourMinuteToDate() else null
        }
        val endTime = with (day_end_time_textView.text) {
            if (isNotEmpty()) toString().hourMinuteToDate() else null
        }
        val breakTime = with (day_break_time_textView.text) {
            if (isNotEmpty()) toString().hourMinuteToDate() else null
        }
        val note = note_editText.text.toString()

        detailWork.beginTime = if (detailWork.workFlag) beginTime else null
        detailWork.endTime = if (detailWork.workFlag) endTime else null
        detailWork.breakTime = if (detailWork.workFlag) breakTime else null
        detailWork.duration = WorksheetManager.calculateDuration(detailWork)
        detailWork.workFlag = detailWork.workFlag
        detailWork.note = if (detailWork.workFlag) note else null

        worksheet.detailWorkList[index] = detailWork

        // 勤務日合計
        worksheet.workDaySum = worksheet.detailWorkList
                .filter { it.workFlag }
                .count()

        // 勤務時間合計
        worksheet.workTimeSum = worksheet.detailWorkList
                .filter { it.workFlag && it.duration != null }
                .sumByDouble { it.duration!! }

        val resultIntent = Intent()
        resultIntent.putExtra(INTENT_WORKSHEET_RETURN_VALUE, worksheet)
        setResult(RESULT_OK, resultIntent)
        finish()
    }

    private fun setDetailWork() {
        with(detailWork) {
            var beginTimeString = if (beginTime != null) beginTime!!.hourMinute() else ""
            var endTimeString = if (endTime != null) endTime!!.hourMinute() else ""
            var breakTimeString = if (breakTime != null) breakTime!!.hourMinute() else ""
            var durationString = if (duration != null) duration.toString() else ""
            if (beginTimeString.isEmpty() && endTimeString.isEmpty() && breakTimeString.isEmpty() && durationString.isEmpty()) {
                val prefsManager = PrefsManager(this@DailyWorkActivity)
                // TODO: リファクタリング
                beginTimeString = prefsManager.defaultBeginTime
                endTimeString = prefsManager.defaultEndTime
                breakTimeString = "01:00"
                val defaultBeginTime = beginTimeString.hourMinuteToDate()
                val defaultEndTime = endTimeString.hourMinuteToDate()
                val defaultBreakTime = breakTimeString.hourMinuteToDate()
                durationString = WorksheetManager.calculateDuration(defaultBeginTime, defaultEndTime, defaultBreakTime).toString()
            }

            day_start_time_textView.text = beginTimeString
            day_end_time_textView.text = endTimeString
            day_break_time_textView.text = breakTimeString
            day_total_time_textView.text = durationString
            note_editText.setText(detailWork.note)
        }

        container_begin_time.setOnClickListener {
            showAddDialog(R.string.set_beginTime_title, day_start_time_textView, TimePickerDialog.WorkTimeType.BEGIN_TIME)
        }

        container_end_time.setOnClickListener {
            showAddDialog(R.string.set_endTime_title, day_end_time_textView, TimePickerDialog.WorkTimeType.END_TIME)
        }

        container_break_time.setOnClickListener {
            showAddDialog(R.string.set_breakTime_title, day_break_time_textView, TimePickerDialog.WorkTimeType.BREAK_TIME)
        }
    }

    private fun showAddDialog(titleId: Int, textView: TextView,
                              workTimeType: TimePickerDialog.WorkTimeType) {
        TimePickerDialog(this)
            .title(titleId)
            .show(textView.text.toString(), workTimeType) {
                textView.text = it

                val beginTime = with (day_start_time_textView.text) {
                    if (isNotEmpty()) toString().hourMinuteToDate() else null
                }
                val endTime = with (day_end_time_textView.text) {
                    if (isNotEmpty()) toString().hourMinuteToDate() else null
                }
                val breakTime = with (day_break_time_textView.text) {
                    if (isNotEmpty()) toString().hourMinuteToDate() else null
                }

                if (beginTime != null && endTime != null && breakTime != null) {
                    val duration = WorksheetManager.calculateDuration(beginTime, endTime, breakTime)
                    day_total_time_textView.text = duration.toString()
                }
            }
    }

    companion object {
        const val INTENT_DETAILWORK_INDEX = "index"
        const val INTENT_DETAILWORK_VALUE = "day"
        const val INTENT_WORKSHEET_RETURN_VALUE = "worksheet_return_value"

        fun newIntent(context: Context, index: Int, worksheet: Worksheet): Intent {
            val intent = Intent(context, DailyWorkActivity::class.java)
            intent.putExtra(INTENT_DETAILWORK_VALUE, worksheet)
            intent.putExtra(INTENT_DETAILWORK_INDEX, index)
            return intent
        }
    }
}
