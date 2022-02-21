package uz.javokhirdev.svocabulary.presentation.flashcards

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import uz.javokhirdev.svocabulary.data.UIState
import uz.javokhirdev.svocabulary.data.model.CardModel
import uz.javokhirdev.svocabulary.data.onSuccess
import uz.javokhirdev.svocabulary.repository.CardsRepository
import javax.inject.Inject

@HiltViewModel
class FlashcardsVM @Inject constructor(
    private val cardsRepository: CardsRepository
) : ViewModel() {

    private val cardsData = MutableStateFlow<UIState<List<CardModel>>>(UIState.Idle)
    val cards = cardsData.asStateFlow()

    fun getCards(setId: Long) {
        viewModelScope.launch {
            cardsRepository.getCards(setId).collectLatest {
                it onSuccess {
                    cardsData.value = it
                    cardsData.value = UIState.Idle
                }
            }
        }
    }
}