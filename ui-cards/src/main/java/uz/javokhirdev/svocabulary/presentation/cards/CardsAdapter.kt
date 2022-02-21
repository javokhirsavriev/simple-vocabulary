package uz.javokhirdev.svocabulary.presentation.cards

import android.content.Context
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import uz.javokhirdev.extensions.inflater
import uz.javokhirdev.extensions.onClick
import uz.javokhirdev.svocabulary.data.model.CardModel
import uz.javokhirdev.svocabulary.presentation.cards.databinding.ItemCardBinding

class CardsAdapter(
    context: Context,
    private val listener: CardListener
) : PagingDataAdapter<CardModel, CardsAdapter.ViewHolder>(DiffCallback()) {

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
                root.setOnLongClickListener {
                    getItem(layoutPosition)?.let { listener.onCardLongClick(it) }
                    true
                }
            }
        }

        fun bind(item: CardModel) {
            with(binding) {
                textTerm.text = item.term
                textDefinition.text = item.definition
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<CardModel>() {
        override fun areItemsTheSame(
            oldItem: CardModel,
            newItem: CardModel
        ): Boolean = oldItem.id == newItem.id

        override fun areContentsTheSame(
            oldItem: CardModel,
            newItem: CardModel
        ): Boolean = oldItem == newItem
    }

    interface CardListener {
        fun onCardClick(item: CardModel)

        fun onCardLongClick(item: CardModel)
    }
}