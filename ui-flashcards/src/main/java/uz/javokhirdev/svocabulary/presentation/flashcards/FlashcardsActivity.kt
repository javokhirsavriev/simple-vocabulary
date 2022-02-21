package uz.javokhirdev.svocabulary.presentation.flashcards

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.view.animation.OvershootInterpolator
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import uz.javokhirdev.extensions.*
import uz.javokhirdev.svocabulary.data.NOT_ID
import uz.javokhirdev.svocabulary.data.SET_ID
import uz.javokhirdev.svocabulary.data.UIState
import uz.javokhirdev.svocabulary.data.model.CardModel
import uz.javokhirdev.svocabulary.data.onSuccess
import uz.javokhirdev.svocabulary.presentation.components.R
import uz.javokhirdev.svocabulary.presentation.flashcards.data.Presets
import uz.javokhirdev.svocabulary.presentation.flashcards.data.TipsSheet
import uz.javokhirdev.svocabulary.presentation.flashcards.databinding.FragmentFlashcardsBinding
import uz.javokhirdev.svocabulary.swipecards.SwipeFlingAdapterView
import uz.javokhirdev.svocabulary.utils.colorFilter
import kotlin.math.abs

@AndroidEntryPoint
class FlashcardsActivity : AppCompatActivity(), View.OnLayoutChangeListener {

    private val binding by lazy { FragmentFlashcardsBinding.inflate(layoutInflater) }

    private val viewModel by viewModels<FlashcardsVM>()

    private val handler = Handler(Looper.getMainLooper())

    private var flashcardsAdapter: FlashcardsAdapter? = null

    private val forgotList = ArrayList<CardModel>()
    private val itemsList = ArrayList<CardModel>()

    private val cardRotationDuration = 100L
    private var currentRound = 1
    private var forgotCount = 0
    private var rememberCount = 0
    private var sideTextWidth = 0
    private var setId = NOT_ID

    private var isCardAnimated = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setId = intent.extras?.getLong(SET_ID, NOT_ID) ?: NOT_ID

        configureClickListeners()

        with(viewModel) {
            getCards(setId)

            repeatingJobOnStarted { cards.collectLatest { onCardsState(it) } }
        }
    }

    override fun onLayoutChange(
        view: View,
        left: Int,
        top: Int,
        right: Int,
        bottom: Int,
        oldLeft: Int,
        oldTop: Int,
        oldRight: Int,
        oldBottom: Int
    ) {
        view.removeOnLayoutChangeListener(this)
        sideTextWidth = view.width
    }

    private fun configureClickListeners() {
        with(binding) {
            "Round $currentRound".also { textRound.text = it }

            textSideTwo.layoutParams.width = 0
            textSideTwoNumber.alpha = 0.5f

            "Side ".also {
                textSideOne.text = it
                textSideTwo.text = it
            }

            textSideOne.isLaidOut.ifFalse {
                textSideOne.addOnLayoutChangeListener(this@FlashcardsActivity)
            }

            textSideOne.isLayoutRequested.ifTrue {
                textSideOne.addOnLayoutChangeListener(this@FlashcardsActivity)
            }

            sideTextWidth = textSideOne.width

            buttonClose.onClick { finish() }
            buttonInfo.onClick { showTipsSheet() }
            buttonForgot.onClick { tapForgot() }
            buttonRemember.onClick { tapRemember() }
            sideContainer.onClick { switchLangPair() }
        }
    }

    private fun configureFlashcardsAdapter() {
        flashcardsAdapter = FlashcardsAdapter(this)
        flashcardsAdapter?.let {
            with(binding) {
                swipeView.setAdapter(it)
                swipeView.setFlingListener(object : SwipeFlingAdapterView.SwipeFlingListener {
                    override fun removeFirstObjectInAdapter() {
                        itemsList.removeAt(0)
                        flashcardsAdapter?.notifyDataSetChanged()
                        resetRememberForgetButtons()
                    }

                    override fun onLeftCardExit(dataObject: Any?) {
                        if (dataObject is CardModel) {
                            forgotCount += 1
                            textForgotCount.text = forgotCount.toString()
                            forgotList.add(dataObject)
                        }
                    }

                    override fun onRightCardExit(dataObject: Any?) {
                        rememberCount += 1
                        textRememberCount.text = rememberCount.toString()
                    }

                    override fun onAdapterAboutToEmpty(itemsInAdapter: Int) {
                        if (itemsInAdapter != 0) return

                        if (forgotList.isNotEmpty()) {
                            itemsList.addAll(forgotList)
                            forgotList.clear()

                            flashcardsAdapter?.setInitialItemsCount(itemsList.size)
                            flashcardsAdapter?.notifyDataSetChanged()

                            currentRound += 1

                            roundContainer.layoutParams.height = roundContainer.height
                            "Round $currentRound".also { textRoundSpec.text = it }

                            textRoundSpec.y = roundContainer.height.toFloat()
                            textRoundSpec.alpha = 0.0f

                            textRound
                                .animate()
                                .y(-(roundContainer.height.toFloat()))
                                .alpha(0.0f)
                                .duration = 300

                            textRoundSpec
                                .animate()
                                .y(0.0f)
                                .alpha(1.0f)
                                .setDuration(300)
                                .withEndAction {
                                    run {
                                        "Round $currentRound".also { textRound.text = it }
                                        textRound.y = 0.0f
                                        textRound.alpha = 1.0f
                                        textRoundSpec.alpha = 0.0f
                                    }
                                }

                            swipeView.scaleX = 0.01f
                            swipeView.scaleY = 0.01f
                            swipeView
                                .animate()
                                .scaleX(1.0f)
                                .scaleY(1.0f)
                                .setDuration(400).interpolator = DecelerateInterpolator()

                            rememberCount = 0
                            textRememberCount.text = ""

                            forgotCount = 0
                            textForgotCount.text = ""

                            return
                        } else {
                            finishedGame()
                        }
                    }

                    override fun onScroll(scrollProgressPercent: Float) {
                        try {
                            swipeView.selectedView?.let { view ->
                                with(FlashcardsAdapter.ViewHolder(view)) {

                                    val i = when {
                                        scrollProgressPercent > 0f -> 1
                                        scrollProgressPercent == 0f -> 0
                                        else -> -1
                                    }

                                    if (i > 0) {
                                        backgroundOverlay.background
                                            .mutate()
                                            .colorFilter(color(R.color.colorGreen))
                                    } else {
                                        backgroundOverlay.background
                                            .mutate()
                                            .colorFilter(color(R.color.colorRed))
                                    }
                                    backgroundOverlay.alpha = abs(scrollProgressPercent)
                                    rootContainer.clearAnimation()
                                    background.clearAnimation()

                                    val abs = abs(scrollProgressPercent) * 0.4f + 0.6f

                                    when {
                                        scrollProgressPercent < 0f -> buttonForgot.alpha = abs
                                        i > 0 -> buttonRemember.alpha = abs
                                        else -> resetRememberForgetButtons()
                                    }
                                }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                })

                swipeView.setOnItemClickListener(object :
                    SwipeFlingAdapterView.OnItemClickListener {
                    override fun onItemClicked(itemPosition: Int, dataObject: Any?) {
                        swipeView.selectedView?.let { view ->
                            val viewHolder = FlashcardsAdapter.ViewHolder(view)
                            view.isEnabled = false
                            rotateCard(viewHolder)
                        }
                    }
                })
            }
        }

        val runSwipe = Runnable { run { binding.swipeView.selectedView?.isEnabled = false } }
        val runFirstCard = Runnable { run { firstCardRotationHint() } }

        handler.post(runSwipe)
        handler.postDelayed(runFirstCard, 500L)

        flashcardsAdapter?.setItems(itemsList)
        flashcardsAdapter?.notifyDataSetChanged()
    }

    private fun onCardsState(uiState: UIState<List<CardModel>>) {
        uiState onSuccess {
            itemsList.clear()
            itemsList.addAll(data.orEmpty())

            configureFlashcardsAdapter()
        }
    }

    private fun firstCardRotationHint() {
        isCardAnimated = true

        val swipeFlingAdapterView = binding.swipeView
        val childAt = swipeFlingAdapterView.getChildAt(swipeFlingAdapterView.childCount - 1)
        val viewHolder = FlashcardsAdapter.ViewHolder(childAt)

        viewHolder.rootView
            .animate()
            .rotationY(40.0f)
            .setDuration(300)
            .setInterpolator(DecelerateInterpolator())
            .withEndAction {
                run {
                    viewHolder.rootView
                        .animate()
                        .setInterpolator(AccelerateInterpolator())
                        .rotationY(0.0f)
                        .setDuration(300)
                        .withEndAction {
                            run {
                                isCardAnimated = false
                                binding.swipeView.selectedView?.isEnabled = true
                            }
                        }
                }
            }
    }

    private fun rotateCard(viewHolder: FlashcardsAdapter.ViewHolder) {
        isCardAnimated = true

        viewHolder.rootView
            .animate()
            .rotationY(90.0f)
            .setDuration(cardRotationDuration)
            .setInterpolator(LinearInterpolator())
            .withEndAction {
                runOnUiThread {
                    run {
                        with(viewHolder) {
                            if (textFront.isVisible) {
                                textFront.beInvisible()
                                textBack.beVisible()
                                return@runOnUiThread
                            }

                            textFront.beVisible()
                            textBack.beInvisible()
                        }
                    }
                }

                with(viewHolder) {
                    rootView.rotationY = -90.0f
                    rootView.animate()
                        .setInterpolator(LinearInterpolator())
                        .rotationY(0.0f)
                        .setDuration(cardRotationDuration)
                        .withEndAction {
                            run {
                                with(binding) {
                                    swipeView.selectedView?.isEnabled = true
                                    isCardAnimated = false
                                }
                            }
                        }
                }
            }
    }

    private fun resetRememberForgetButtons() {
        with(binding) {
            buttonForgot.alpha = 0.6f
            buttonRemember.alpha = 0.6f
        }
    }

    private fun tapForgot() {
        if (!isCardAnimated) {
            binding.swipeView.topCardListener?.selectLeft()
        }
    }

    private fun tapRemember() {
        if (!isCardAnimated) {
            binding.swipeView.topCardListener?.selectRight()
        }
    }

    private fun switchLangPair() {
        with(binding) {
            swipeView.selectedView?.isEnabled.orFalse().ifTrue {
                flashcardsAdapter?.let { adapter ->
                    adapter.isInverted().ifTrue {
                        textSideOneNumber
                            .animate()
                            .alpha(1f)
                            .duration = 200

                        textSideTwoNumber
                            .animate()
                            .alpha(0.5f)
                            .duration = 200

                        animateWidth(textSideOne)

                        textSideTwo.layoutParams.width = 0
                    }.ifFalse {
                        textSideOneNumber
                            .animate()
                            .alpha(0.5f)
                            .duration = 200

                        textSideTwoNumber
                            .animate()
                            .alpha(1f)
                            .duration = 200

                        textSideOne.layoutParams.width = 0

                        animateWidth(textSideTwo)
                    }

                    val isInverted = !adapter.isInverted()
                    adapter.setInverted(isInverted)
                    adapter.notifyDataSetChanged()

                    swipeView.children.forEach { view ->
                        val viewHolder = FlashcardsAdapter.ViewHolder(view)
                        viewHolder.textFront.beInvisibleIf(isInverted)
                        viewHolder.textBack.beInvisibleIf(!isInverted)
                    }

                    swipeView.selectedView?.let {
                        val viewHolder = FlashcardsAdapter.ViewHolder(it)
                        viewHolder.textFront.beInvisibleIf(!isInverted)
                        viewHolder.textBack.beInvisibleIf(isInverted)
                        rotateCard(viewHolder)
                    }
                }
            }
        }
        return
    }

    private fun animateWidth(view: View) {
        val duration = ValueAnimator
            .ofInt(view.width, sideTextWidth)
            .setDuration(200L)
        duration.addUpdateListener {
            view.layoutParams.width = (it.animatedValue as Int).toInt()
            view.requestLayout()
        }

        val animatorSet = AnimatorSet()
        animatorSet.interpolator = OvershootInterpolator()
        animatorSet.play(duration)
        animatorSet.start()
    }

    private fun finishedGame() {
        lifecycleScope.launch {
            binding.konfettiView.start(Presets.rain())
            delay(3000L)
            finish()
        }
    }

    private fun showTipsSheet() {
        val dialog = TipsSheet()
        supportFragmentManager.run { dialog.show(this, dialog.tag) }
    }
}