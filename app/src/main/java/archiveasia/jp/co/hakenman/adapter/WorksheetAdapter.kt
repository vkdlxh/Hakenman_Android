package archiveasia.jp.co.hakenman.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import archiveasia.jp.co.hakenman.R
import archiveasia.jp.co.hakenman.extension.day
import archiveasia.jp.co.hakenman.extension.dayOfWeek
import archiveasia.jp.co.hakenman.extension.hourMinute
import archiveasia.jp.co.hakenman.extension.week
import archiveasia.jp.co.hakenman.model.DetailWork
import kotlinx.android.synthetic.main.month_worksheet_item.view.*

class WorksheetAdapter(context: Context,
                       private val detailWorkList: MutableList<DetailWork>): BaseAdapter() {

    private val inflater: LayoutInflater
            = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val rowView = inflater.inflate(R.layout.month_worksheet_item, parent, false)
        val detailWork = getItem(position) as DetailWork

        val dayTextView = rowView.day_textView
        dayTextView.text = detailWork.workDate.day()
        val weekTextView = rowView.week_textView
        weekTextView.text = detailWork.workDate.week()
        val textColor = when (detailWork.workDate.dayOfWeek()) {
            1 -> Color.RED
            7 -> Color.BLUE
            else -> Color.BLACK
        }
        weekTextView.setTextColor(textColor)
        val workFlagTextView = rowView.workFlag_textView
        workFlagTextView.text =  if (detailWork.workFlag) "O" else "X"

        val startWorkTextView = rowView.startWork_textView
        detailWork.beginTime?.let {
            startWorkTextView.text = it.hourMinute()
        }

        val endWorkTextView = rowView.endWork_textView
        detailWork.endTime?.let {
            endWorkTextView.text = it.hourMinute()
        }

        val breakTimeTextView = rowView.breakTime_textView
        detailWork.breakTime?.let {
            breakTimeTextView.text = it.hourMinute()
        }

        val workTimeTextView = rowView.workTime_textView
        detailWork.duration?.let {
            workTimeTextView.text = it.toString()
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