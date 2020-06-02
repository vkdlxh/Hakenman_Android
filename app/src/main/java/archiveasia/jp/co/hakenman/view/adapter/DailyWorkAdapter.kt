package archiveasia.jp.co.hakenman.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import archiveasia.jp.co.hakenman.R
import archiveasia.jp.co.hakenman.extension.day
import archiveasia.jp.co.hakenman.extension.dayOfWeek
import archiveasia.jp.co.hakenman.extension.hourMinute
import archiveasia.jp.co.hakenman.extension.week
import archiveasia.jp.co.hakenman.extension.yearMonth
import archiveasia.jp.co.hakenman.model.DetailWork
import archiveasia.jp.co.hakenman.view.activity.MonthlyWorkActivity
import kotlinx.android.synthetic.main.item_daliy_work.view.*

class DailyWorkAdapter(
    private val detailWorkList: MutableList<DetailWork> = mutableListOf(),
    private val listener: MonthlyWorkActivity.SheetCalendarItemClickListener
) : RecyclerView.Adapter<DailyWorkAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_daliy_work, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = detailWorkList.count()

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindView(detailWorkList[position])
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    fun replaceDailyWorkList(list: List<DetailWork>) {
        val diffResult = DiffUtil.calculateDiff(DiffCallback(detailWorkList, list))
        detailWorkList.clear()
        detailWorkList.addAll(list)
        diffResult.dispatchUpdatesTo(this)
    }

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {

        fun bindView(detailWork: DetailWork) {
            itemView.setOnClickListener {
                listener.onClick(adapterPosition)
            }
            with (detailWork) {
                itemView.day_textView.text = workDate.day()
                itemView.week_textView.apply {
                    text = workDate.week()
                    val resId = when (workDate.dayOfWeek()) {
                        1 -> R.color.sun_color
                        7 -> R.color.sat_color
                        else -> R.color.text_color_on_background
                    }
                    setTextColor(ContextCompat.getColor(context, resId))
                }
                itemView.workFlag_textView.text =  if (workFlag) "O" else "X"
                beginTime?.let {
                    itemView.startWork_textView.text = it.hourMinute()
                }
                endTime?.let {
                    itemView.endWork_textView.text = it.hourMinute()
                }

                breakTime?.let {
                    itemView.breakTime_textView.text = it.hourMinute()
                }
                duration?.let {
                    itemView.workTime_textView.text = it.toString()
                }
                itemView.note_textView.text = note
            }
        }
    }

    class DiffCallback(
        private val oldData: List<DetailWork>,
        private val newData: List<DetailWork>
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
}
