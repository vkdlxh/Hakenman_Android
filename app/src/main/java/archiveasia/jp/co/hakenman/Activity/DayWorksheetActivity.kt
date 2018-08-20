package archiveasia.jp.co.hakenman.Activity

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.constraint.Group
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.NumberPicker
import android.widget.TextView
import android.widget.TimePicker
import archiveasia.jp.co.hakenman.Extension.hourMinute
import archiveasia.jp.co.hakenman.Extension.hourMinuteToDate
import archiveasia.jp.co.hakenman.Extension.hourMinuteToDouble
import archiveasia.jp.co.hakenman.Manager.PrefsManager
import archiveasia.jp.co.hakenman.Manager.WorksheetManager
import archiveasia.jp.co.hakenman.Model.DetailWork
import archiveasia.jp.co.hakenman.Model.Worksheet
import archiveasia.jp.co.hakenman.R
import kotlinx.android.synthetic.main.activity_day_worksheet.*
import kotlinx.android.synthetic.main.timepicker_dialog.view.*
import java.util.*

const val INTENT_DETAILWORK_INDEX = "index"
const val INTENT_DETAILWORK_VALUE = "day"
const val INTENT_WORKSHEET_RETURN_VALUE = "worksheet_return_value"

class DayWorksheetActivity : AppCompatActivity() {

    private var index: Int = -1
    private lateinit var worksheet: Worksheet
    private lateinit var detailWork: DetailWork

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_day_worksheet)

        index = intent.getIntExtra(INTENT_DETAILWORK_INDEX, index)
        worksheet = intent.getParcelableExtra(INTENT_DETAILWORK_VALUE)
        detailWork = worksheet.detailWorkList[index]
        setDetailWork()

        worksheet_form_view.visibility = if (detailWork.workFlag) View.VISIBLE else View.INVISIBLE
        isWork_switch.isChecked = detailWork.workFlag

        isWork_switch.setOnCheckedChangeListener { _, isChecked ->
            isEditableWorksheet(isChecked)
        }

        title = getString(R.string.day_work_activity_title)
                .format(detailWork.workYear, detailWork.workMonth, detailWork.workDay)
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
        }
        return super.onOptionsItemSelected(item)
    }

    private fun isEditableWorksheet(isWork: Boolean) {
        if (isWork) {
            worksheet_form_view.visibility = View.VISIBLE
            detailWork.workFlag = true
        } else {
            worksheet_form_view.visibility = View.INVISIBLE
            detailWork.workFlag = false
        }
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

        // TODO: 저장한 근무표를 근무표 리스트에 넣고 제이슨 파일에 덮어쓴다.
        worksheet.detailWorkList.set(index, detailWork)
        var resultIntent = Intent()
        resultIntent.putExtra(INTENT_WORKSHEET_RETURN_VALUE, worksheet)
        setResult(RESULT_OK, resultIntent)
        finish()
    }

    private fun setDetailWork() {
        day_start_time_textView.text = if (detailWork.beginTime != null) detailWork.beginTime!!.hourMinute() else ""
        day_end_time_textView.text = if (detailWork.endTime != null) detailWork.endTime!!.hourMinute() else ""
        day_break_time_textView.text = if (detailWork.breakTime != null) detailWork.breakTime!!.hourMinute() else ""
        day_total_time_textView.text = if (detailWork.duration != null) detailWork.duration.toString() else ""
        note_editText.setText(if (detailWork.note != null) detailWork.note else "")

        beginTime_view.setOnClickListener {
            showAddDialog("開始時間登録", day_start_time_textView, false)
        }

        endTime_view.setOnClickListener {
            showAddDialog("終了時間登録", day_end_time_textView, false)
        }

        breakTime_view.setOnClickListener {
            showAddDialog("休憩時間登録", day_break_time_textView, true)
        }
    }

    private fun setTimePickerInterval(timePicker: TimePicker) {
        var minuteID = Resources.getSystem().getIdentifier("minute", "id", "android")
        var minutePicker = timePicker.findViewById<NumberPicker>(minuteID)

        val interval = PrefsManager(this).interval
        var numValue = 60 / interval
        var displayedValue = arrayListOf<String>()

        for (i in 0..numValue) {
            val value = i * interval
            displayedValue.add(i, value.toString())
        }

        minutePicker.minValue = 0
        minutePicker.maxValue = numValue - 1
        minutePicker.displayedValues = displayedValue.toTypedArray()
    }

    private fun showAddDialog(title: String, textView: TextView, isBreakTimePickerView: Boolean) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.timepicker_dialog, null)
        dialogView.time_picker.setIs24HourView(true)

        if (isBreakTimePickerView) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                dialogView.time_picker.time_picker.hour = 0
                dialogView.time_picker.time_picker.minute = 0
            } else {
                dialogView.time_picker.time_picker.currentHour = 0
                dialogView.time_picker.time_picker.currentMinute = 0
            }
        }
        setTimePickerInterval(dialogView.time_picker)
        val addDialog = AlertDialog.Builder(this)

        with (addDialog) {
            setView(dialogView)
            setTitle(title + "登録")

            setPositiveButton("登録") {
                dialog, whichButton ->
                textView.text = getPickerTime(dialogView).hourMinute()

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
                    var beginTime = beginTime!!.time
                    var endTime = endTime!!.time
                    var breakTime = breakTime!!.hourMinuteToDouble()
                    val workTime = (endTime - beginTime) / (60 * 60 * 1000)
                    val result = workTime.toDouble() - breakTime
                    day_total_time_textView.text = result.toString()
                }

                dialog.dismiss()
            }

            setNegativeButton("キャンセル") {
                dialog, whichButton ->
                dialog.dismiss()
            }

            create()
            show()
        }
    }

    private fun getPickerTime(view: View): Date {
        val hour = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            view.time_picker.hour
        } else {
            view.time_picker.currentHour
        }
        val minute = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            view.time_picker.minute * PrefsManager(this).interval
        } else {
            view.time_picker.currentMinute * PrefsManager(this).interval
        }
        var cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, hour)
        cal.set(Calendar.MINUTE, minute)
        return cal.time
    }

    companion object {

        fun newIntent(context: Context, index: Int, worksheet: Worksheet): Intent {
            val intent = Intent(context, DayWorksheetActivity::class.java)
            intent.putExtra(INTENT_DETAILWORK_VALUE, worksheet)
            intent.putExtra(INTENT_DETAILWORK_INDEX, index)
            return intent
        }
    }
}
