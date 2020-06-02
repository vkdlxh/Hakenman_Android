package archiveasia.jp.co.hakenman.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import archiveasia.jp.co.hakenman.R
import archiveasia.jp.co.hakenman.extension.day
import archiveasia.jp.co.hakenman.extension.hourMinute
import archiveasia.jp.co.hakenman.extension.yearMonth
import archiveasia.jp.co.hakenman.model.DetailWork
import archiveasia.jp.co.hakenman.model.Worksheet
import archiveasia.jp.co.hakenman.view.activity.MonthlyWorkActivity
import kotlinx.android.synthetic.main.item_calendar_day.view.*
import java.util.Date

class CalendarAdapter(
    context: Context,
    private val dateArray: List<Date>,
    private var worksheet: Worksheet,
    private val clickListener: MonthlyWorkActivity.SheetCalendarItemClickListener
) : ArrayAdapter<Date>(context, R.layout.item_calendar_day) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(parent.context).inflate(R.layout.item_calendar_day, null).apply {
            tag = ViewHolder(this)
        }

        val holder = view.tag as ViewHolder
        holder.bindView(dateArray[position])

        // Cell サイズ固定
        val param = AbsListView.LayoutParams(parent.width / 7, parent.height / 6)
        view.layoutParams = param

        return view
    }

    override fun getCount(): Int {
        return dateArray.count()
    }

    fun replaceWorksheet(worksheet: Worksheet) {
        this.worksheet = worksheet
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val view: View) {
        private val dayTextView = view.day_textView
        private val noteFlagImageView = view.note_flag_image_view
        private val startTimeTextView = view.start_time_textView
        private val endTimeTextView = view.end_time_textView

        fun bindView(date: Date) {
            val currentMonth = worksheet.workDate.yearMonth() == date.yearMonth()
            dayTextView.apply {
                text = date.day()
                val textColor = ContextCompat.getColor(context,
                    if (currentMonth) R.color.text_color_on_background else R.color.text_color_on_surface)
                setTextColor(textColor)
            }
            if (currentMonth) {
                val day = date.day()
                var position = -1
                var detailWork: DetailWork? = null
                for ((index, value) in worksheet.detailWorkList.withIndex()) {
                    if (value.workDate.day() == day) {
                        position = index
                        detailWork = value
                        break
                    }
                }
                detailWork?.let {
                    detailWork.beginTime?.let {
                        startTimeTextView.visibility = View.VISIBLE
                        startTimeTextView.text = it.hourMinute()
                    } ?: run {
                        startTimeTextView.visibility = View.INVISIBLE
                    }
                    detailWork.endTime?.let {
                        endTimeTextView.visibility = View.VISIBLE
                        endTimeTextView.text = it.hourMinute()
                    } ?: run {
                        endTimeTextView.visibility = View.INVISIBLE
                    }
                    noteFlagImageView.visibility = if (detailWork.note.isNullOrEmpty()) View.INVISIBLE else View.VISIBLE
                    view.setOnClickListener {
                        clickListener.onClick(position)
                    }
                }

                /*
                worksheet.detailWorkList.find {
                    it.workDate.day() == day
                }?.let { detailWork ->
                    detailWork.beginTime?.let {
                        startTimeTextView.visibility = View.VISIBLE
                        startTimeTextView.text = it.hourMinute()
                    } ?: run {
                        startTimeTextView.visibility = View.INVISIBLE
                    }
                    detailWork.endTime?.let {
                        endTimeTextView.visibility = View.VISIBLE
                        endTimeTextView.text = it.hourMinute()
                    } ?: run {
                        endTimeTextView.visibility = View.INVISIBLE
                    }
                    noteFlagImageView.visibility = if (detailWork.note.isNullOrEmpty()) View.INVISIBLE else View.VISIBLE
                    view.setOnClickListener {
                        clickListener.onClick()
                    }
                }
                 */
            } else {
                noteFlagImageView.visibility = View.INVISIBLE
                startTimeTextView.visibility = View.INVISIBLE
                endTimeTextView.visibility = View.INVISIBLE
            }
        }
    }
}