package uz.javokhirdev.svocabulary.presentation.sets

import android.content.Context
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import uz.javokhirdev.extensions.beVisibleIf
import uz.javokhirdev.extensions.inflater
import uz.javokhirdev.extensions.onClick
import uz.javokhirdev.svocabulary.data.model.SetModel
import uz.javokhirdev.svocabulary.presentation.sets.databinding.ItemSetBinding

class SetListAdapter(
    context: Context,
    private val listener: SetListener
) : PagingDataAdapter<SetModel, SetListAdapter.ViewHolder>(DiffCallback()) {

    private val inflater = context.inflater

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        ItemSetBinding.inflate(inflater, parent, false)
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

    inner class ViewHolder(private val binding: ItemSetBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            with(binding) {
                root.onClick { getItem(layoutPosition)?.let { listener.onSetClick(it) } }
            }
        }

        fun bind(item: SetModel) {
            with(binding) {
                textDescription.beVisibleIf(!item.description.isNullOrEmpty())

                textTitle.text = item.title
                textDescription.text = item.description
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<SetModel>() {
        override fun areItemsTheSame(
            oldItem: SetModel,
            newItem: SetModel
        ): Boolean = oldItem.id == newItem.id

        override fun areContentsTheSame(
            oldItem: SetModel,
            newItem: SetModel
        ): Boolean = oldItem == newItem
    }

    interface SetListener {
        fun onSetClick(item: SetModel)
    }
}