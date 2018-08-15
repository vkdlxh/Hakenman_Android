package archiveasia.jp.co.hakenman.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import archiveasia.jp.co.hakenman.Model.DetailWork
import archiveasia.jp.co.hakenman.R
import kotlinx.android.synthetic.main.work_sheet_item.view.*
import java.text.SimpleDateFormat

class WorksheetAdapter(private val context: Context,
                       private val detailWorkList: MutableList<DetailWork>): BaseAdapter() {

    private val inflater: LayoutInflater
            = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val rowView = inflater.inflate(R.layout.work_sheet_item, parent, false)
        val detailWork = getItem(position) as DetailWork

        // TODO: 일별 월일 값에서오름차순 정리

        val dayTextView = rowView.day_textView
        dayTextView.text = detailWork.workDay.toString()
        val weekTextView = rowView.week_textView
        weekTextView.text = detailWork.workWeek // TODO: 週変換
        val workFlagTextView = rowView.workFlag_textView
        workFlagTextView.text =  if (detailWork.workFlag == true) {
            "○"
        } else {
            "×"
        }

        val startWorkTextView = rowView.startWork_textView
        if (detailWork.beginTime != null) {
            startWorkTextView.text = SimpleDateFormat("HH:mm").format(detailWork.beginTime)
        }

        val endWorkTextView = rowView.endWork_textView
        if (detailWork.endTime != null) {
            endWorkTextView.text = SimpleDateFormat("HH:mm").format(detailWork.endTime)
        }

        val workTimeTextView = rowView.workTime_textView
        if (detailWork.beginTime != null && detailWork.endTime != null && detailWork.breakTime != null) {
            var beginTime = detailWork.beginTime!!.time
            var endTime = detailWork.endTime!!.time
            val workTime = (endTime - beginTime) / (60 * 60 * 1000)
            val finalWorkTime = workTime.toDouble() - detailWork.breakTime!!
            workTimeTextView.text = finalWorkTime.toString()
        }

        val noteTextView = rowView.note_textView
        noteTextView.text = detailWork.note

        return rowView
    }

    override fun getItem(position: Int): Any {
        return detailWorkList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return detailWorkList.size
    }
}