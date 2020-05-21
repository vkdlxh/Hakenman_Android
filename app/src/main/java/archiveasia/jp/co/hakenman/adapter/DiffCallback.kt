package archiveasia.jp.co.hakenman.adapter

import androidx.recyclerview.widget.DiffUtil
import archiveasia.jp.co.hakenman.extension.yearMonth
import archiveasia.jp.co.hakenman.model.Worksheet

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