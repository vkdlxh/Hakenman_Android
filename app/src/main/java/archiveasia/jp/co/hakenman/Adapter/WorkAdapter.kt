package archiveasia.jp.co.hakenman.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import archiveasia.jp.co.hakenman.Extension.year
import archiveasia.jp.co.hakenman.Model.Worksheet
import archiveasia.jp.co.hakenman.R
import kotlinx.android.synthetic.main.top_list_item.view.*

class WorkAdapter(private val context: Context,
                  private val workList: MutableList<Worksheet>): BaseAdapter() {

    private val inflater: LayoutInflater
            = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val rowView = inflater.inflate(R.layout.top_list_item, parent, false)
        val work = getItem(position) as Worksheet

        val yearTextView = rowView.year_textView
        yearTextView.text = work.workDate.year()
        val weekTextViewrowView = rowView.header_week_textView

        val dayTextViewrowView = rowView.header_day_textView

        val workHourTextViewrowView = rowView.workHour_textView
        val workDayTextViewrowView = rowView.workDay_textView


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