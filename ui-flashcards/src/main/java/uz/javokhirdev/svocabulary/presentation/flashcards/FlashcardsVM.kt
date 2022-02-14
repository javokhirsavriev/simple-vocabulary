package uz.javokhirdev.svocabulary.presentation.flashcards

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import uz.javokhirdev.svocabulary.data.UIState
import uz.javokhirdev.svocabulary.data.model.CardModel
import uz.javokhirdev.svocabulary.data.onFailure
import uz.javokhirdev.svocabulary.data.onLoading
import uz.javokhirdev.svocabulary.data.onSuccess
import uz.javokhirdev.svocabulary.preferences.FlashcardStoreRepository
import uz.javokhirdev.svocabulary.repository.CardsRepository
import uz.javokhirdev.svocabulary.utils.TTSManager
import javax.inject.Inject

@HiltViewModel
class FlashcardsVM @Inject constructor(
    private val flashcardStoreRepository: FlashcardStoreRepository,
    private val cardsRepository: CardsRepository,
    private val ttsManager: TTSManager
) : ViewModel() {

    private val isFlashcardStartedData = MutableStateFlow<UIState<Boolean>>(UIState.Idle)
    val isFlashcardStarted = isFlashcardStartedData.asStateFlow()

    private val cardsData = MutableStateFlow<UIState<List<CardModel>>>(UIState.Idle)
    val cards = cardsData.asStateFlow()

    private val currentCardData = MutableStateFlow<UIState<CardModel>>(UIState.Idle)
    val currentCard = currentCardData.asStateFlow()

    private val countData = MutableLiveData<CountData>()
    val count: LiveData<CountData> get() = countData

    private val finishedData = MutableLiveData<FinishedData>()
    val finished: LiveData<FinishedData> get() = finishedData

    private val allCards = ArrayList<CardModel>()
    private val correctCards = ArrayList<CardModel>()
    private val incorrectCards = ArrayList<CardModel>()

    private var currentPosition = 0

    init {
        getFlashcardStarted()
    }

    private fun getFlashcardStarted() {
        viewModelScope.launch {
            flashcardStoreRepository.isFlashcardStarted.collectLatest {
                isFlashcardStartedData.value = UIState.success(it)
            }
        }
    }

    fun setFlashcardStarted() {
        viewModelScope.launch {
            flashcardStoreRepository.setFlashcardStarted(true)
        }
    }

    fun getCards(setId: Long) {
        viewModelScope.launch {
            cardsRepository.getCards(setId).collectLatest {
                it onLoading {
                    cardsData.value = it
                } onSuccess {
                    data?.let { list -> if (!list.isNullOrEmpty()) startFlashcards(list) }
                } onFailure {
                    cardsData.value = it
                }
            }
        }
    }

    private fun startFlashcards(data: List<CardModel>) {
        clearAllData()
        allCards.addAll(data)
        getCurrentCard()
    }

    private fun finishFlashcards() {
        currentPosition = 0
        currentCardData.value = UIState.Idle

        val countData = getCountData()

        finishedData.value = FinishedData(
            isFinished = true,
            all = countData.all,
            correct = countData.correct,
            incorrect = countData.incorrect
        )
    }

    fun correctCurrentTerm(isCorrect: Boolean) {
        currentCardData.value onSuccess {
            data?.let { card ->
                if (isCorrect) {
                    correctCards.add(card)
                } else {
                    incorrectCards.add(card)
                }

                currentPosition++
                getCurrentCard()
            }
        }
    }

    fun sayCurrentTerm() {
        currentCardData.value onSuccess {
            ttsManager.say((data?.term ?: data?.definition).orEmpty())
        }
    }

    fun continueIncorrectFlashcards() {
        val list = incorrectCards.shuffled()

        if (!list.isNullOrEmpty()) startFlashcards(list)
    }

    private fun getCurrentCard() {
        if (currentPosition == allCards.size) {
            finishFlashcards()
        } else {
            currentCardData.value = UIState.success(allCards.getOrNull(currentPosition))
            setCount()
        }
    }

    private fun setCount() {
        countData.value = getCountData()
    }

    private fun getCountData() = CountData(
        current = currentPosition,
        all = allCards.size,
        correct = correctCards.size,
        incorrect = incorrectCards.size
    )

    private fun clearAllData() {
        allCards.clear()
        correctCards.clear()
        incorrectCards.clear()

        currentPosition = 0
    }
}