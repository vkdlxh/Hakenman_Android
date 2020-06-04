package archiveasia.jp.co.hakenman.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import archiveasia.jp.co.hakenman.databinding.ItemTutorialStepBinding
import archiveasia.jp.co.hakenman.model.Step

class TutorialViewPagerAdapter(
    private val items: List<Step>
) : RecyclerView.Adapter<TutorialViewPagerAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemTutorialStepBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    class ViewHolder(private val binding: ItemTutorialStepBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(step: Step) {
            binding.titleTextView.text = step.title
            binding.descriptionTextView.text = step.description
            binding.imageView.setImageResource(step.image)
            itemView.setBackgroundColor(ContextCompat.getColor(itemView.context, step.backgroundColor))
        }
    }
}