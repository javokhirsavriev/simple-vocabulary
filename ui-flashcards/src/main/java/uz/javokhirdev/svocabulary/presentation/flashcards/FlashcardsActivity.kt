package uz.javokhirdev.svocabulary.presentation.flashcards

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import uz.javokhirdev.extensions.*
import uz.javokhirdev.svocabulary.data.*
import uz.javokhirdev.svocabulary.data.model.CardModel
import uz.javokhirdev.svocabulary.presentation.components.R
import uz.javokhirdev.svocabulary.presentation.flashcards.databinding.ActivityFlashcardsBinding

@AndroidEntryPoint
class FlashcardsActivity : AppCompatActivity() {

    private val binding by lazy { ActivityFlashcardsBinding.inflate(layoutInflater) }

    private val viewModel by viewModels<FlashcardsVM>()

    private var setId = NOT_ID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setId = intent.extras?.getLong(SET_ID, NOT_ID) ?: NOT_ID

        configureClickListeners()

        with(viewModel) {
            repeatingJobOnStarted { isFlashcardStarted.collectLatest { onFlashcardStartedState(it) } }

            cards.observe(this@FlashcardsActivity) { onCardsState(it) }
            currentCard.observe(this@FlashcardsActivity) { onCurrentCardState(it) }
            count.observe(this@FlashcardsActivity) { onCountState(it) }
            finished.observe(this@FlashcardsActivity) { onFinishedState(it) }
        }
    }

    private fun configureClickListeners() {
        with(binding) {
            buttonClose.onClick { finish() }
            buttonInfo.onClick {
                val dialog = QuizTipsDialog()
                supportFragmentManager.run { dialog.show(this, dialog.tag) }
            }
            buttonStart.onClick { viewModel.setFlashcardStarted() }
            buttonCancel.onClick { viewModel.correctCurrentTerm(false) }
            buttonDone.onClick { viewModel.correctCurrentTerm(true) }
            buttonVolume.onClick { viewModel.sayCurrentTerm() }
            buttonReset.onClick { viewModel.getCards(setId) }

            textFront.autoSize(32)
            textBack.autoSize(32)
        }
    }

    private fun onFlashcardStartedState(uiState: UIState<Boolean>) {
        uiState onSuccess {
            data.orFalse().ifTrue {
                viewModel.getCards(setId)
            }.ifFalse {
                showHideViews(
                    isStartVisible = true
                )
            }
        }
    }

    private fun onCardsState(uiState: UIState<List<CardModel>>) {
        with(binding) {
            showHideViews()

            uiState onLoading {
                loadingView.onLoading(isLoading)
            } onFailure {
                loadingView.onFailure(message)
            }
        }
    }

    private fun onCurrentCardState(uiState: UIState<CardModel>) {
        uiState onSuccess {
            setCurrentCard(data)
        }
    }

    private fun onCountState(data: CountData) {
        with(binding) {
            "${data.current + 1} / ${data.all}".also { textCount.text = it }
            textCountCorrect.text = data.correct.toString()
            textCountIncorrect.text = data.incorrect.toString()
        }
    }

    private fun onFinishedState(data: FinishedData) {
        data.isFinished.ifTrue {
            showHideViews(
                isFinishVisible = true
            )

            when (data.correct) {
                data.all -> {
                    with(binding) {
                        buttonReset.beGone()
                        buttonContinue.setButtonText(getString(R.string.study_again))
                        buttonContinue.onClick { viewModel.getCards(setId) }

                        textFinishEmoji.text = getString(R.string.emoji_1)
                        textFinishDescription.text = getString(R.string.congratulations)
                    }
                }
                0 -> {
                    with(binding) {
                        buttonReset.beVisible()
                        buttonContinue.setButtonText(getString(R.string.continue_str))
                        buttonContinue.onClick { viewModel.continueIncorrectFlashcards() }

                        textFinishEmoji.text = getString(R.string.emoji_2)
                        "Keep practising to master ${data.incorrect} remaining terms.".also {
                            textFinishDescription.text = it
                        }
                    }
                }
                else -> {
                    with(binding) {
                        buttonReset.beVisible()
                        buttonContinue.setButtonText(getString(R.string.continue_str))
                        buttonContinue.onClick { viewModel.continueIncorrectFlashcards() }

                        textFinishEmoji.text = getString(R.string.emoji_2)
                        "You just learnt ${data.correct} term! Keep practising to master ${data.incorrect} remaining term.".also {
                            textFinishDescription.text = it
                        }
                    }
                }
            }
        }
    }

    private fun setCurrentCard(data: CardModel? = null) {
        setCurrentTermDefinition(data)

        showHideViews(
            isInfoVisible = true,
            isGameVisible = true
        )
    }

    private fun setCurrentTermDefinition(data: CardModel? = null) {
        with(binding) {
            textFront.text = data?.term.orEmpty()
            textBack.text = data?.definition.orEmpty()
        }
    }

    private fun showHideViews(
        isInfoVisible: Boolean = false,
        isStartVisible: Boolean = false,
        isFinishVisible: Boolean = false,
        isGameVisible: Boolean = false,
    ) {
        with(binding) {
            buttonInfo.beVisibleIf(isInfoVisible)
            layoutStart.beVisibleIf(isStartVisible)
            layoutFinish.beVisibleIf(isFinishVisible)
            textCount.beVisibleIf(isGameVisible)
            layoutGame.beVisibleIf(isGameVisible)
        }
    }
}