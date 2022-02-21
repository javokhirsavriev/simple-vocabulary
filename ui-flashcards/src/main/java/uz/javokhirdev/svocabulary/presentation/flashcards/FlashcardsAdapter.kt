package uz.javokhirdev.svocabulary.presentation.flashcards

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import uz.javokhirdev.extensions.*
import uz.javokhirdev.svocabulary.data.model.CardModel
import uz.javokhirdev.svocabulary.presentation.flashcards.databinding.ItemFlashcardBinding

class FlashcardsAdapter(
    private val context: Context
) : BaseAdapter() {

    private val inflater = context.inflater

    private var items = ArrayList<CardModel>()

    private val cardAppearAnimationDuration: Long = 300
    private var initialItemsCount = 0
    private val initialTextsAlpha = 0.05f
    private var isInverted = false

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        val view: View?
        val viewHolder: ViewHolder

        if (convertView == null) {
            view = inflater.inflate(R.layout.item_flashcard, parent, false)

            viewHolder = ViewHolder(view)
            viewHolder.rootView.cameraDistance =
                context.resources.displayMetrics.density * 8000.toFloat()

            view.tag = viewHolder
        } else {
            view = convertView
            viewHolder = view.tag as ViewHolder
        }

        with(viewHolder) {
            textFront.text = items[position].term.orEmpty()
            textBack.text = items[position].definition.orEmpty()

            textFront.beInvisibleIf(isInverted)
            textBack.beInvisibleIf(!isInverted)

            when {
                position == 0 -> {
                    background.alpha = 0.7f
                    rootContainer.rotation = 3.0f

                    background
                        .animate()
                        .alpha(1.0f)
                        .setDuration(cardAppearAnimationDuration)
                        .interpolator = DecelerateInterpolator()

                    rootContainer
                        .animate()
                        .rotation(0.0f)
                        .setDuration(cardAppearAnimationDuration)
                        .interpolator = DecelerateInterpolator()

                    rootView.beVisible()

                    textFront
                        .animate()
                        .alpha(1.0f)
                        .setDuration(cardAppearAnimationDuration)
                        .interpolator = DecelerateInterpolator()

                    textBack
                        .animate()
                        .alpha(1.0f)
                        .setDuration(cardAppearAnimationDuration)
                        .interpolator = DecelerateInterpolator()

                    textCount
                        .animate()
                        .alpha(1.0f)
                        .setDuration(cardAppearAnimationDuration)
                        .interpolator = DecelerateInterpolator()

                    buttonPlay
                        .animate()
                        .alpha(1.0f)
                        .setDuration(cardAppearAnimationDuration)
                        .interpolator = DecelerateInterpolator()
                }
                position == 1 -> {
                    textFront.alpha = initialTextsAlpha
                    textBack.alpha = initialTextsAlpha
                    textCount.alpha = initialTextsAlpha
                    buttonPlay.alpha = initialTextsAlpha
                    background.alpha = 0.7f
                    rootContainer.rotation = 3.0f
                    rootView.beVisible()
                }
                position != 2 -> {
                    rootView.beInvisible()
                }
                else -> {
                    textFront.alpha = initialTextsAlpha
                    textBack.alpha = initialTextsAlpha
                    textCount.alpha = initialTextsAlpha
                    buttonPlay.alpha = initialTextsAlpha
                    background.alpha = 0.7f
                    rootContainer.rotation = -3.0f
                    rootView.beVisible()
                }
            }

//            viewHolder.btnPlaySound.setOnClickListener(
//                `CardsAdapter$getView$1`(
//                    this,
//                    viewHolder,
//                    position
//                )
//            )

            if (position == 0) viewHolder.buttonPlay.performClick()

            val sb = StringBuilder()
            sb.append(initialItemsCount + position - items.size + 1)
            sb.append('/')
            sb.append(initialItemsCount)
            viewHolder.textCount.text = sb.toString()

            textFront.autoSize(40)
            textBack.autoSize(40)
        }

        return view
    }

    override fun getItem(position: Int): CardModel? = items.getOrNull(position)

    override fun getCount(): Int = items.size

    class ViewHolder(view: View) {
        private val binding = ItemFlashcardBinding.bind(view)

        val background: View = binding.background
        val backgroundOverlay: View = binding.backgroundOverlay
        val buttonPlay: ImageView = binding.buttonPlay
        val textCount: TextView = binding.textCount
        val textFront: TextView = binding.textFront
        val textBack: TextView = binding.textBack
        val rootContainer: View = binding.rootContainer
        val rootView: View = binding.rootView
    }

    fun setItems(arrayList: ArrayList<CardModel>) {
        this.items = arrayList
        this.initialItemsCount = arrayList.size
    }

    fun setInitialItemsCount(initialItemsCount: Int) {
        this.initialItemsCount = initialItemsCount
    }

    fun isInverted(): Boolean = isInverted

    fun setInverted(isInverted: Boolean) {
        this.isInverted = isInverted
    }
}