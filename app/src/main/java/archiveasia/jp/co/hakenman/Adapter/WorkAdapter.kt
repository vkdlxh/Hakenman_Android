package archiveasia.jp.co.hakenman.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import archiveasia.jp.co.hakenman.Model.Work
import archiveasia.jp.co.hakenman.R

class WorkAdapter(private val context: Context,
                  private val workList: Array<Work>): BaseAdapter() {

    private val inflater: LayoutInflater
            = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val rowView = inflater.inflate(R.layout.top_list_item, parent, false)
        val work = getItem(position) as Work

        val yearTextView = rowView.findViewById(R.id.year_textView) as TextView
//        yearTextView.text = work.workDate.subSequence(0, 4) // 年だけ
        val weekTextViewrowView = rowView.findViewById(R.id.header_week_textView) as TextView
        val dayTextViewrowView = rowView.findViewById(R.id.header_day_textView) as TextView

        val workHourTextViewrowView = rowView.findViewById(R.id.workHour_textView) as TextView
        val workDayTextViewrowView = rowView.findViewById(R.id.workDay_textView) as TextView


        return rowView
    }

    override fun getItem(position: Int): Any {
        return workList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return workList.size
    }
}