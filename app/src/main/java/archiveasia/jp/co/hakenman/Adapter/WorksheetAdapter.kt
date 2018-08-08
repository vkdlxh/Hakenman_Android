package archiveasia.jp.co.hakenman.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import archiveasia.jp.co.hakenman.Model.DetailWork
import archiveasia.jp.co.hakenman.R

class WorksheetAdapter(private val context: Context,
                       private val detailWorkList: ArrayList<DetailWork>): BaseAdapter() {

    private val inflater: LayoutInflater
            = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val rowView = inflater.inflate(R.layout.work_sheet_item, parent, false)
        val detailWork = getItem(position) as DetailWork

        // TODO: 일별 월일 값에서오름차순 정리

        val dayTextView = rowView.findViewById(R.id.day_textView) as TextView
        dayTextView.text = detailWork.workDay.toString()
        val weekTextView = rowView.findViewById(R.id.week_textView) as TextView
        weekTextView.text = detailWork.workWeek // TODO: 週変換
        val workFlagTextView = rowView.findViewById(R.id.workFlag_textView) as TextView
        workFlagTextView.text =  if (detailWork.workFlag == true) {
            "○"
        } else {
            "×"
        }
        val startWorkTextView = rowView.findViewById(R.id.startWork_textView) as TextView
        startWorkTextView.text = detailWork.beginTime   // TODO: Date化するかも
        val endWorkTextView = rowView.findViewById(R.id.endWork_textView) as TextView
        endWorkTextView.text = detailWork.endTime   // TODO: Date化するかも
        val workTimeTextView = rowView.findViewById(R.id.workTime_textView) as TextView
        // TODO: ( endTime(string -> Date) - startTime(string -> Date) ) - breakTime(Int)
//        workTimeTextView.text =
        val noteTextView = rowView.findViewById(R.id.note_textView) as TextView
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