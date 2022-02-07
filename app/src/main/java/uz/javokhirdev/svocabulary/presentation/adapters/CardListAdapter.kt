package uz.javokhirdev.svocabulary.presentation.adapters

import android.content.Context
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import uz.javokhirdev.extensions.inflater
import uz.javokhirdev.extensions.onClick
import uz.javokhirdev.svocabulary.data.db.cards.CardEntity
import uz.javokhirdev.svocabulary.databinding.ItemCardBinding

class CardListAdapter(
    context: Context,
    private val listener: CardListener
) : PagingDataAdapter<CardEntity, CardListAdapter.ViewHolder>(DiffCallback()) {

    private val inflater = context.inflater

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        ItemCardBinding.inflate(inflater, parent, false)
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

    inner class ViewHolder(private val binding: ItemCardBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            with(binding) {
                root.onClick { getItem(layoutPosition)?.let { listener.onCardClick(it) } }
                buttonVolume.onClick { getItem(layoutPosition)?.let { listener.onVolumeClick(it) } }
            }
        }

        fun bind(item: CardEntity) {
            with(binding) {
                textTerm.text = item.term
                textDefinition.text = item.definition
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<CardEntity>() {
        override fun areItemsTheSame(
            oldItem: CardEntity,
            newItem: CardEntity
        ): Boolean = oldItem.id == newItem.id

        override fun areContentsTheSame(
            oldItem: CardEntity,
            newItem: CardEntity
        ): Boolean = oldItem == newItem
    }

    interface CardListener {
        fun onCardClick(item: CardEntity)

        fun onVolumeClick(item: CardEntity)
    }
}