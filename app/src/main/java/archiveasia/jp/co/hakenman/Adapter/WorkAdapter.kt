package archiveasia.jp.co.hakenman.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import archiveasia.jp.co.hakenman.Extension.month
import archiveasia.jp.co.hakenman.Extension.year
import archiveasia.jp.co.hakenman.Model.Worksheet
import archiveasia.jp.co.hakenman.R
import kotlinx.android.synthetic.main.worksheet_list_item.view.*

class WorkAdapter(private val context: Context,
                  private val workList: List<Worksheet>): BaseAdapter() {

    private val inflater: LayoutInflater
            = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val rowView = inflater.inflate(R.layout.worksheet_list_item, parent, false)
        val work = getItem(position) as Worksheet

        val yearTextView = rowView.year_textView
        yearTextView.text = work.workDate.year()
        val monthTextView = rowView.month_textView
        monthTextView.text = work.workDate.month()
        val workHourTextViewrowView = rowView.workHour_textView
        workHourTextViewrowView.text = work.workTimeSum.toString()
        val workDayTextViewrowView = rowView.workDay_textView
        workDayTextViewrowView.text = work.workDaySum.toString()

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