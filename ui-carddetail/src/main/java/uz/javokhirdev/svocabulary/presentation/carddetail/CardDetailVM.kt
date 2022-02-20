package uz.javokhirdev.svocabulary.presentation.carddetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import uz.javokhirdev.svocabulary.data.UIState
import uz.javokhirdev.svocabulary.data.model.CardModel
import uz.javokhirdev.svocabulary.repository.CardsRepository
import javax.inject.Inject

@HiltViewModel
class CardDetailVM @Inject constructor(
    private val repository: CardsRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val args = CardDetailFragmentArgs.fromSavedStateHandle(savedStateHandle)

    private val cardData = MutableStateFlow<UIState<CardModel>>(UIState.Idle)
    val card = cardData.asStateFlow()

    private val createData = MutableStateFlow<UIState<Boolean>>(UIState.Idle)
    val create = createData.asStateFlow()

    init {
        getCardById()
    }

    private fun getCardById() {
        viewModelScope.launch {
            repository.getCardById(args.cardId).collectLatest { cardData.value = it }
        }
    }

    fun createCard(
        isNewCreate: Boolean,
        term: String,
        definition: String
    ) {
        viewModelScope.launch {
            repository.insertOrUpdate(
                isNewCreate = isNewCreate,
                cardId = args.cardId,
                setId = args.setId,
                term = term,
                definition = definition
            ).collectLatest { createData.value = it }
        }
    }
}