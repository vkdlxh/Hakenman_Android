package archiveasia.jp.co.hakenman.Activity

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.constraint.Group
import android.view.View
import archiveasia.jp.co.hakenman.Extension.hourMinute
import archiveasia.jp.co.hakenman.Model.DetailWork
import archiveasia.jp.co.hakenman.R
import kotlinx.android.synthetic.main.activity_day_worksheet.*

const val INTENT_WORK_DAY = "day"

class DayWorksheetActivity : AppCompatActivity() {

    private lateinit var detailWork: DetailWork

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_day_worksheet)

        detailWork = intent.getParcelableExtra(INTENT_WORK_DAY)
        setDetailWork()
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
            println("click group")
        }
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
