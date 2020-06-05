package archiveasia.jp.co.hakenman.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import archiveasia.jp.co.hakenman.databinding.ItemTutorialStepBinding
import archiveasia.jp.co.hakenman.model.Step
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.CenterInside
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

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

        private val context = itemView.context

        fun bind(step: Step) {
            binding.titleTextView.text = context.getText(step.title)
            binding.descriptionTextView.text = context.getText(step.description)
            binding.imageView.setImageResource(step.image)
            // システム言語を変えるとイメージが変わらないためキャッシュを使わない
            Glide.with(context)
                    .load(step.image)
                    .transform(CenterInside(), RoundedCorners(18))
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .into(binding.imageView)
            itemView.setBackgroundColor(ContextCompat.getColor(context, step.backgroundColor))
        }
    }
}