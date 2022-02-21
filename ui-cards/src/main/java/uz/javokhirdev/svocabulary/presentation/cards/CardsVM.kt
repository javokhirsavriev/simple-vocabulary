package uz.javokhirdev.svocabulary.presentation.cards

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import uz.javokhirdev.svocabulary.data.UIState
import uz.javokhirdev.svocabulary.data.model.CardModel
import uz.javokhirdev.svocabulary.repository.CardsRepository
import javax.inject.Inject

@HiltViewModel
class CardsVM @Inject constructor(
    private val repository: CardsRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val args = CardsFragmentArgs.fromSavedStateHandle(savedStateHandle)

    private val deleteData = MutableStateFlow<UIState<Boolean>>(UIState.Idle)
    val delete = deleteData.asStateFlow()

    private val clearAllData = MutableStateFlow<UIState<Boolean>>(UIState.Idle)
    val clearAll = clearAllData.asStateFlow()

    fun getCards(keywords: String): Flow<PagingData<CardModel>> = repository.getCards(
        setId = args.setId,
        keywords = keywords
    ).cachedIn(viewModelScope)

    fun deleteCard(cardId: Long) {
        viewModelScope.launch {
            repository.delete(cardId).collectLatest { deleteData.value = it }
        }
    }

    fun clearAll() {
        viewModelScope.launch {
            repository.deleteCardsBySetId(args.setId).collectLatest { clearAllData.value = it }
        }
    }
}