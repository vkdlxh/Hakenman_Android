package archiveasia.jp.co.hakenman.view.activity

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
import archiveasia.jp.co.hakenman.databinding.ActivityDailyWorkBinding
import archiveasia.jp.co.hakenman.extension.day
import archiveasia.jp.co.hakenman.extension.hourMinute
import archiveasia.jp.co.hakenman.extension.hourMinuteToDate
import archiveasia.jp.co.hakenman.extension.month
import archiveasia.jp.co.hakenman.extension.viewBinding
import archiveasia.jp.co.hakenman.extension.year
import archiveasia.jp.co.hakenman.manager.PrefsManager
import archiveasia.jp.co.hakenman.manager.WorksheetManager
import archiveasia.jp.co.hakenman.model.DetailWork
import archiveasia.jp.co.hakenman.model.Worksheet

class DailyWorkActivity : AppCompatActivity() {

    private val binding by viewBinding(ActivityDailyWorkBinding::inflate)

    private var index: Int = -1
    private lateinit var worksheet: Worksheet
    private lateinit var detailWork: DetailWork

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        index = intent.getIntExtra(INTENT_DETAILWORK_INDEX, index)
        worksheet = intent.getParcelableExtra(INTENT_DETAILWORK_VALUE)
        detailWork = worksheet.detailWorkList[index]
        setDetailWork()

        title = getString(R.string.day_work_activity_title)
            .format(detailWork.workDate.year(), detailWork.workDate.month(), detailWork.workDate.day())
        binding.worksheetFormView.visibility = if (detailWork.workFlag) View.VISIBLE else View.INVISIBLE
        binding.isWorkSwitch.isChecked = detailWork.workFlag

        binding.isWorkSwitch.setOnCheckedChangeListener { _, isChecked ->
            binding.worksheetFormView.visibility = if (isChecked) View.VISIBLE else View.INVISIBLE
            detailWork.workFlag = isChecked
        }

        CustomLog.d("1日詳細勤務表画面")
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.add_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
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
        val beginTime = with (binding.dayStartTimeTextView.text) {
            if (isNotEmpty()) toString().hourMinuteToDate() else null
        }
        val endTime = with (binding.dayEndTimeTextView.text) {
            if (isNotEmpty()) toString().hourMinuteToDate() else null
        }
        val breakTime = with (binding.dayBreakTimeTextView.text) {
            if (isNotEmpty()) toString().hourMinuteToDate() else null
        }
        val note = binding.noteEditText.text.toString()

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

            binding.dayStartTimeTextView.text = beginTimeString
            binding.dayEndTimeTextView.text = endTimeString
            binding.dayBreakTimeTextView.text = breakTimeString
            binding.dayTotalTimeTextView.text = durationString
            binding.noteEditText.setText(detailWork.note)
        }

        binding.containerBeginTime.setOnClickListener {
            showAddDialog(R.string.set_beginTime_title, binding.dayStartTimeTextView, TimePickerDialog.WorkTimeType.BEGIN_TIME)
        }

        binding.containerEndTime.setOnClickListener {
            showAddDialog(R.string.set_endTime_title, binding.dayEndTimeTextView, TimePickerDialog.WorkTimeType.END_TIME)
        }

        binding.containerBreakTime.setOnClickListener {
            showAddDialog(R.string.set_breakTime_title, binding.dayBreakTimeTextView, TimePickerDialog.WorkTimeType.BREAK_TIME)
        }
    }

    private fun showAddDialog(titleId: Int, textView: TextView,
                              workTimeType: TimePickerDialog.WorkTimeType) {
        TimePickerDialog(this)
            .title(titleId)
            .show(textView.text.toString(), workTimeType) {
                textView.text = it

                val beginTime = with (binding.dayStartTimeTextView.text) {
                    if (isNotEmpty()) toString().hourMinuteToDate() else null
                }
                val endTime = with (binding.dayEndTimeTextView.text) {
                    if (isNotEmpty()) toString().hourMinuteToDate() else null
                }
                val breakTime = with (binding.dayBreakTimeTextView.text) {
                    if (isNotEmpty()) toString().hourMinuteToDate() else null
                }

                if (beginTime != null && endTime != null && breakTime != null) {
                    val duration = WorksheetManager.calculateDuration(beginTime, endTime, breakTime)
                    binding.dayTotalTimeTextView.text = duration.toString()
                }
            }
    }

    companion object {
        const val INTENT_WORKSHEET_RETURN_VALUE = "worksheet_return_value"

        private const val INTENT_DETAILWORK_INDEX = "index"
        private const val INTENT_DETAILWORK_VALUE = "day"

        fun createIntent(context: Context, index: Int, worksheet: Worksheet) =
            Intent(context, DailyWorkActivity::class.java).apply {
                putExtra(INTENT_DETAILWORK_VALUE, worksheet)
                putExtra(INTENT_DETAILWORK_INDEX, index)
            }
    }
}
