package archiveasia.jp.co.hakenman.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import archiveasia.jp.co.hakenman.R
import archiveasia.jp.co.hakenman.databinding.ItemCalendarDayBinding
import archiveasia.jp.co.hakenman.extension.day
import archiveasia.jp.co.hakenman.extension.hourMinute
import archiveasia.jp.co.hakenman.extension.yearMonth
import archiveasia.jp.co.hakenman.model.DetailWork
import archiveasia.jp.co.hakenman.model.Worksheet
import archiveasia.jp.co.hakenman.view.activity.MonthlyWorkActivity
import java.util.Date

class CalendarAdapter(
    context: Context,
    private val dateArray: List<Date>,
    private var worksheet: Worksheet,
    private val clickListener: MonthlyWorkActivity.SheetCalendarItemClickListener
) : ArrayAdapter<Date>(context, R.layout.item_calendar_day) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        if (view == null) {
            val binding = ItemCalendarDayBinding.inflate(LayoutInflater.from(context), parent, false)
            view = binding.root
            view.tag = ViewHolder(binding)
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

    inner class ViewHolder(private val binding: ItemCalendarDayBinding) {

        fun bindView(date: Date) {
            val currentMonth = worksheet.workDate.yearMonth() == date.yearMonth()
            binding.dayTextView.apply {
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
                        binding.startTimeTextView.visibility = View.VISIBLE
                        binding.startTimeTextView.text = it.hourMinute()
                    } ?: run {
                        binding.startTimeTextView.visibility = View.INVISIBLE
                    }
                    detailWork.endTime?.let {
                        binding.endTimeTextView.visibility = View.VISIBLE
                        binding.endTimeTextView.text = it.hourMinute()
                    } ?: run {
                        binding.endTimeTextView.visibility = View.INVISIBLE
                    }
                    binding.noteFlagImageView.visibility = if (detailWork.note.isNullOrEmpty()) View.INVISIBLE else View.VISIBLE
                    binding.root.setOnClickListener {
                        clickListener.onClick(position)
                    }
                }
            } else {
                binding.noteFlagImageView.visibility = View.INVISIBLE
                binding.startTimeTextView.visibility = View.INVISIBLE
                binding.endTimeTextView.visibility = View.INVISIBLE
            }
        }
    }
}