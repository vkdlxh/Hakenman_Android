package archiveasia.jp.co.hakenman.Activity

import android.app.Activity
import android.app.TimePickerDialog
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
import archiveasia.jp.co.hakenman.Extension.month
import archiveasia.jp.co.hakenman.Extension.year
import archiveasia.jp.co.hakenman.Manager.PrefsManager
import archiveasia.jp.co.hakenman.Model.DetailWork
import archiveasia.jp.co.hakenman.Model.Worksheet
import archiveasia.jp.co.hakenman.R
import kotlinx.android.synthetic.main.activity_day_worksheet.*
import kotlinx.android.synthetic.main.timepicker_dialog.view.*

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

        // フォムを見えなくする
        worksheet_form_view.visibility = if (detailWork.workFlag) View.VISIBLE else View.INVISIBLE
        isWork_switch.isChecked = detailWork.workFlag

        isWork_switch.setOnCheckedChangeListener { _, isChecked ->
            isEditableWorksheet(isChecked)
        }

        title = getString(R.string.day_work_activity_title)
                .format(detailWork.workYear, detailWork.workMonth, detailWork.workDay)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.activity_actions, menu)
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
            if (isNotEmpty()) toString().toDouble() else null
        }
        val note = note_editText.text.toString()

        if (detailWork.workFlag) {
            detailWork.beginTime = beginTime
            detailWork.endTime = endTime
            detailWork.breakTime = breakTime
            detailWork.workFlag = detailWork.workFlag
            detailWork.note = note
        } else {
            detailWork.beginTime = null
            detailWork.endTime = null
            detailWork.breakTime = null
            detailWork.workFlag = detailWork.workFlag
            detailWork.note = null
        }
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
        day_break_time_textView.text = if (detailWork.breakTime != null) detailWork.breakTime.toString() else ""
        day_total_time_textView.text = if (detailWork.beginTime != null && detailWork.endTime != null && detailWork.breakTime != null) {
            var beginTime = detailWork.beginTime!!.time
            var endTime = detailWork.endTime!!.time
            val workTime = (endTime - beginTime) / (60 * 60 * 1000)
            (workTime.toDouble() - detailWork.breakTime!!).toString()
        } else {
            ""
        }
        note_editText.setText(if (detailWork.note != null) detailWork.note else "")

        start_group.addOnClickListener {
            showAddDialog("開始時間登録", day_start_time_textView)
            println("click group")
        }

        end_group.addOnClickListener {
            showAddDialog("終了時間登録", day_end_time_textView)
            println("click end group")
        }
    }

    private fun setTimePickerInterval(timePicker: TimePicker) {
        var minuteID = Resources.getSystem().getIdentifier("minute", "id", "android")
        var minutePicker = timePicker.findViewById<NumberPicker>(minuteID)

        val interval = PrefsManager(this).interval // TODO: 유저 디폴트에 설정된 값 가져오는 걸로 수정.
        var numValue = 60 / interval
        var displayedValue = arrayListOf<String>()

        for (i in 0..numValue) {
            val value = i * interval
            displayedValue.add(value.toString())
        }

        minutePicker.minValue = 0
        minutePicker.maxValue = numValue - 1
        minutePicker.displayedValues = displayedValue.toTypedArray()
    }

    private fun showAddDialog(title: String, textView: TextView) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.timepicker_dialog, null)
        dialogView.time_picker.setIs24HourView(true)
        setTimePickerInterval(dialogView.time_picker)
        val addDialog = AlertDialog.Builder(this)

        // TODO: 인터벌 정보(유저 디폴트?) 가져와서 설
        // TODO: 휴계시간일 때 픽커 뷰 는 0시 0분부터 시작하도록.

        with (addDialog) {
            setView(dialogView)
            setTitle(title + "登録")

            setPositiveButton("登録") {
                dialog, whichButton ->
                textView.text = getPickerTime(dialogView)
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

    private fun getPickerTime(view: View): String {
        val hour = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            view.time_picker.hour
        } else {
            view.time_picker.currentHour
        }
        val minute = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            view.time_picker.minute
        } else {
            view.time_picker.currentMinute
        }
        return "$hour:$minute"
    }

    fun Group.addOnClickListener(listener: (view: View) -> Unit) {
        referencedIds.forEach { id ->
            rootView.findViewById<View>(id).setOnClickListener(listener)
        }
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
