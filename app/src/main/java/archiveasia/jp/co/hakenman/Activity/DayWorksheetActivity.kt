package archiveasia.jp.co.hakenman.Activity

import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.constraint.Group
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import archiveasia.jp.co.hakenman.Extension.hourMinute
import archiveasia.jp.co.hakenman.Model.DetailWork
import archiveasia.jp.co.hakenman.R
import kotlinx.android.synthetic.main.activity_day_worksheet.*
import kotlinx.android.synthetic.main.timepicker_dialog.*
import kotlinx.android.synthetic.main.timepicker_dialog.view.*

const val INTENT_WORK_DAY = "day"

class DayWorksheetActivity : AppCompatActivity() {

    private lateinit var detailWork: DetailWork

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_day_worksheet)

        detailWork = intent.getParcelableExtra(INTENT_WORK_DAY)
        setDetailWork()

        // フォムを見えなくする
        worksheet_form_view.visibility = if (detailWork.workFlag) View.VISIBLE else View.INVISIBLE
        isWork_switch.isChecked = detailWork.workFlag

        isWork_switch.setOnCheckedChangeListener { _, isChecked ->
            isEditableWorksheet(isChecked)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.activity_actions, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.action_add -> {
                // TODO: save worksheet, and close activity
                println("tap add button")
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
            // TODO:
            showAddDialog("開始時間登録", day_start_time_textView)
            println("click group")
        }

        end_group.addOnClickListener {
            // TODO:
            showAddDialog("終了時間登録", day_end_time_textView)
            println("click end group")
        }
    }

    private fun showAddDialog(title: String, textView: TextView) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.timepicker_dialog, null)
        dialogView.time_picker.setIs24HourView(true)
        val addDialog = AlertDialog.Builder(this)

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
        return "$hour : $minute"
    }

    fun Group.addOnClickListener(listener: (view: View) -> Unit) {
        referencedIds.forEach { id ->
            rootView.findViewById<View>(id).setOnClickListener(listener)
        }
    }

    companion object {

        fun newIntent(context: Context, detailWork: DetailWork): Intent {
            val intent = Intent(context, DayWorksheetActivity::class.java)
            intent.putExtra(INTENT_WORK_DAY, detailWork)
            return intent
        }
    }
}
