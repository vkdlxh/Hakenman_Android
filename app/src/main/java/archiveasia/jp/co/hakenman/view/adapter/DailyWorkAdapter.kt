package archiveasia.jp.co.hakenman.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import archiveasia.jp.co.hakenman.R
import archiveasia.jp.co.hakenman.databinding.ItemDaliyWorkBinding
import archiveasia.jp.co.hakenman.extension.day
import archiveasia.jp.co.hakenman.extension.dayOfWeek
import archiveasia.jp.co.hakenman.extension.hourMinute
import archiveasia.jp.co.hakenman.extension.week
import archiveasia.jp.co.hakenman.extension.yearMonth
import archiveasia.jp.co.hakenman.model.DetailWork
import archiveasia.jp.co.hakenman.view.activity.MonthlyWorkActivity

class DailyWorkAdapter(
    private val detailWorkList: MutableList<DetailWork> = mutableListOf(),
    private val listener: MonthlyWorkActivity.SheetCalendarItemClickListener
) : RecyclerView.Adapter<DailyWorkAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemDaliyWorkBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
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

    inner class ViewHolder(private val binding: ItemDaliyWorkBinding): RecyclerView.ViewHolder(binding.root) {

        fun bindView(detailWork: DetailWork) {
            itemView.setOnClickListener {
                listener.onClick(adapterPosition)
            }
            with (detailWork) {
                binding.dayTextView.text = workDate.day()
                binding.weekTextView.apply {
                    text = workDate.week()
                    val resId = when (workDate.dayOfWeek()) {
                        1 -> R.color.sun_color
                        7 -> R.color.sat_color
                        else -> R.color.text_color_on_background
                    }
                    setTextColor(ContextCompat.getColor(context, resId))
                }
                binding.workFlagTextView.text =  if (workFlag) "O" else "X"
                beginTime?.let {
                    binding.startWorkTextView.text = it.hourMinute()
                }
                endTime?.let {
                    binding.endWorkTextView.text = it.hourMinute()
                }
                breakTime?.let {
                    binding.breakTimeTextView.text = it.hourMinute()
                }
                duration?.let {
                    binding.workTimeTextView.text = it.toString()
                }
                binding.noteTextView.text = note
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
