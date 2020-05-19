package archiveasia.jp.co.hakenman.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import archiveasia.jp.co.hakenman.R
import archiveasia.jp.co.hakenman.extension.month
import archiveasia.jp.co.hakenman.extension.year
import archiveasia.jp.co.hakenman.model.DetailWork
import archiveasia.jp.co.hakenman.model.Worksheet
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import kotlinx.android.synthetic.main.worksheet_list_item.view.*

class WorksheetListAdapter(
    private var list: MutableList<Worksheet> = mutableListOf(),
    private var listener: WorksheetListener
) : RecyclerView.Adapter<WorksheetListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val item = layoutInflater.inflate(R.layout.worksheet_list_item, parent, false)
        return ViewHolder(item)
    }

    override fun getItemCount(): Int {
        return list.count()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindView(list[position])
    }

    fun replaceWorksheetList(list: List<Worksheet>) {
        val diffResult = DiffUtil.calculateDiff(DiffCallback(this.list, list))
        this.list.clear()
        this.list.addAll(list)
        diffResult.dispatchUpdatesTo(this)
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bindView(worksheet: Worksheet) {
            // TODO: グラフ実装
            if (adapterPosition == 0) {
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
            } else {
                itemView.line_chart.visibility = View.GONE
            }

            itemView.setOnClickListener {
                listener.onClickItem(adapterPosition, worksheet)
            }
            itemView.setOnLongClickListener {
                listener.onLongClickItem(adapterPosition)
                true
            }

            itemView.year_textView.text = worksheet.workDate.year()
            itemView.month_textView.text = worksheet.workDate.month()
            itemView.workHour_textView.text = worksheet.workTimeSum.toString()
            itemView.workDay_textView.text = worksheet.workDaySum.toString()
        }

        private fun setChart(chartItemList: ArrayList<DetailWork>) {
            itemView.line_chart.apply {
                visibility = View.VISIBLE
                val entries = ArrayList<Entry>()
                chartItemList.forEach {
                    entries.add(Entry(entries.size * 1f, it.duration?.toFloat() ?: 0f))
                }

                // チャートデータライン設定
                val lineDataSet = LineDataSet(entries, context.getString(R.string.work_time_column))
                lineDataSet.lineWidth = 2f
                lineDataSet.color = Color.BLACK
                lineDataSet.circleRadius = 4.5f
                lineDataSet.setCircleColor(Color.BLACK)
                lineDataSet.setDrawCircleHole(false)
                lineDataSet.valueTextSize = 8f

                val data = LineData(lineDataSet)
                this.data = data

                // チャート縦設定
                xAxis.axisMinimum = -0.5f
                xAxis.axisMaximum = 6.5f
                xAxis.setDrawLabels(false)
                xAxis.setDrawGridLines(false)
                xAxis.position = XAxis.XAxisPosition.BOTH_SIDED

                // チャート左側設定
                axisLeft.axisLineColor = Color.GRAY
                axisLeft.labelCount = 4
                axisLeft.setDrawLabels(false)
                axisLeft.axisMaximum = data.yMax + 1f
                axisLeft.axisMinimum = data.yMin - 1f

                // チャート右側設定
                axisRight.axisLineColor = Color.GRAY
                axisRight.setDrawGridLines(false)
                axisRight.setDrawLabels(false)

                description.text = context.getString(R.string.chart_description)
                setNoDataText(context.getString(R.string.chart_no_data))

                setTouchEnabled(false)
                setPinchZoom(false)

                // チャートの空白調節
                // TODO: Magic Numberどうにかしたい
                setViewPortOffsets(1f, viewPortHandler.offsetTop() / 2, 1f, viewPortHandler.offsetBottom())

                invalidate()
            }
        }

    }

    interface WorksheetListener {
        fun onClickItem(index: Int, worksheet: Worksheet)

        fun onLongClickItem(index: Int)
    }
}
