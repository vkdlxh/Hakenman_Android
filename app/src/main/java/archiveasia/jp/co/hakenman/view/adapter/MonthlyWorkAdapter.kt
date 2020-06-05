package archiveasia.jp.co.hakenman.view.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import archiveasia.jp.co.hakenman.R
import archiveasia.jp.co.hakenman.databinding.ItemMonthlyWorkCurrentBinding
import archiveasia.jp.co.hakenman.databinding.ItemMonthlyWorkPastBinding
import archiveasia.jp.co.hakenman.extension.day
import archiveasia.jp.co.hakenman.extension.dayOfWeek
import archiveasia.jp.co.hakenman.extension.month
import archiveasia.jp.co.hakenman.extension.twoDecimalPlaces
import archiveasia.jp.co.hakenman.extension.week
import archiveasia.jp.co.hakenman.extension.year
import archiveasia.jp.co.hakenman.extension.yearMonth
import archiveasia.jp.co.hakenman.model.DetailWork
import archiveasia.jp.co.hakenman.model.Worksheet
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import java.util.Date

class MonthlyWorkAdapter(
    private var list: MutableList<Worksheet> = mutableListOf(),
    private var listener: MonthlyWorkListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_CURRENT_MONTHLY_WORK = 1
        private const val TYPE_PAST_MONTHLY_WORK = 2
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_CURRENT_MONTHLY_WORK -> {
                val binding = ItemMonthlyWorkCurrentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                CurrentMonthlyWorkViewHolder(binding)
            }
            TYPE_PAST_MONTHLY_WORK -> {
                val binding = ItemMonthlyWorkPastBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                PastMonthlyWorkViewHolder(binding)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun getItemCount(): Int {
        return list.count()
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) TYPE_CURRENT_MONTHLY_WORK else TYPE_PAST_MONTHLY_WORK
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = list[position]
        when (holder) {
            is CurrentMonthlyWorkViewHolder -> holder.bindView(item)
            is PastMonthlyWorkViewHolder -> holder.bindView(item)
        }
    }

    fun replaceWorksheetList(list: List<Worksheet>) {
        val diffResult = DiffUtil.calculateDiff(DiffCallback(this.list, list))
        this.list.clear()
        this.list.addAll(list)
        diffResult.dispatchUpdatesTo(this)
    }

    inner class CurrentMonthlyWorkViewHolder(
        private val binding: ItemMonthlyWorkCurrentBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bindView(worksheet: Worksheet) {
            val detailWorkList = worksheet.detailWorkList
            var index = detailWorkList.size - 1
            val chartItemList = ArrayList<DetailWork>()
            while (index != -1) {
                val detailWork = detailWorkList[index]
                if (detailWork.workFlag && detailWork.duration != null && detailWork.duration != 0.0) {
                    chartItemList.add(0, detailWork)
                }
                if (chartItemList.size == 7) {
                    break
                }
                index -= 1
            }
            setChart(chartItemList)

            itemView.setOnClickListener {
                listener.onClickItem(adapterPosition, worksheet)
            }
            itemView.setOnLongClickListener {
                listener.onLongClickItem(adapterPosition)
                true
            }

            binding.yearTextView.text = worksheet.workDate.year()
            binding.monthTextView.text = worksheet.workDate.month()
            val now = Date()
            binding.dayTextView.text = now.day()
            binding.weekTextView.apply {
                text = now.week()
                val color = when(now.dayOfWeek()) {
                    1 -> Color.RED
                    7 -> Color.BLUE
                    else -> Color.BLACK
                }
                setTextColor(color)
            }
            binding.workHourTextView.text = worksheet.workTimeSum.toString()
            binding.workDayTextView.text = worksheet.workDaySum.toString()
        }

        private fun setChart(chartItemList: ArrayList<DetailWork>) {
            binding.lineChart.apply {
                val textColorOnBackground = ContextCompat.getColor(context, R.color.text_color_on_background)
                val textColorOnSurface = ContextCompat.getColor(context, R.color.text_color_on_surface)
                visibility = View.VISIBLE
                val entries = ArrayList<Entry>()
                chartItemList.forEach {
                    entries.add(Entry(entries.size * 1f, it.duration?.toFloat() ?: 0f))
                }

                // チャートデータライン設定
                val lineDataSet = LineDataSet(entries, context.getString(R.string.work_time_column))
                lineDataSet.lineWidth = 2f
                lineDataSet.color = textColorOnBackground
                lineDataSet.circleRadius = 4.5f
                lineDataSet.setCircleColor(textColorOnBackground)
                lineDataSet.valueTextColor = textColorOnBackground
                lineDataSet.setDrawCircleHole(false)
                lineDataSet.valueTextSize = 8f

                val data = LineData(lineDataSet)
                data.setValueFormatter(object : ValueFormatter() {
                    override fun getPointLabel(entry: Entry?): String {
                        return entry?.y?.toDouble()?.twoDecimalPlaces().toString()
                    }
                })

                this.data = data
                if (data.entryCount == 0) {
                    // 「データなし」メッセージ表示のため
                    clear()
                }

                // チャート縦設定
                xAxis.axisMinimum = -0.5f
                xAxis.axisMaximum = 6.5f
                xAxis.setDrawLabels(false)
                xAxis.setDrawGridLines(false)
                xAxis.position = XAxis.XAxisPosition.BOTH_SIDED

                // チャート左側設定
                axisLeft.axisLineColor = textColorOnSurface
                axisLeft.labelCount = 4
                axisLeft.setDrawLabels(false)
                axisLeft.axisMaximum = data.yMax + 1f
                axisLeft.axisMinimum = data.yMin - 1f

                // チャート右側設定
                axisRight.axisLineColor = textColorOnSurface
                axisRight.setDrawGridLines(false)
                axisRight.setDrawLabels(false)

                legend.textColor = textColorOnBackground

                description.text = context.getString(R.string.chart_description)
                description.textColor = textColorOnBackground
                setNoDataText(context.getString(R.string.chart_no_data))
                setNoDataTextColor(textColorOnBackground)

                setTouchEnabled(false)
                setPinchZoom(false)

                invalidate()
            }
        }
    }

    inner class PastMonthlyWorkViewHolder(
        private val binding: ItemMonthlyWorkPastBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bindView(worksheet: Worksheet) {
            itemView.setOnClickListener {
                listener.onClickItem(adapterPosition, worksheet)
            }
            itemView.setOnLongClickListener {
                listener.onLongClickItem(adapterPosition)
                true
            }

            binding.yearTextView.text = worksheet.workDate.year()
            binding.monthTextView.text = worksheet.workDate.month()
            binding.workHourTextView.text = worksheet.workTimeSum.toString()
            binding.workDayTextView.text = worksheet.workDaySum.toString()
        }
    }

    class DiffCallback(
        private val oldData: List<Worksheet>,
        private val newData: List<Worksheet>
    ) : DiffUtil.Callback() {

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldData[oldItemPosition].workDate.yearMonth() == newData[newItemPosition].workDate.yearMonth()
        }

        override fun getOldListSize(): Int = oldData.size

        override fun getNewListSize(): Int = newData.size

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldData[oldItemPosition] == newData[newItemPosition]
        }
    }

    interface MonthlyWorkListener {
        fun onClickItem(index: Int, worksheet: Worksheet)

        fun onLongClickItem(index: Int)
    }
}
