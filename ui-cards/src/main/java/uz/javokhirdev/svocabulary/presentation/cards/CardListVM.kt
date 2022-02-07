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
import uz.javokhirdev.svocabulary.data.model.SetModel
import uz.javokhirdev.svocabulary.repository.CardsRepository
import uz.javokhirdev.svocabulary.repository.SetsRepository
import javax.inject.Inject

@HiltViewModel
class CardListVM @Inject constructor(
    private val cardsRepository: CardsRepository,
    private val setsRepository: SetsRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val args = CardListFragmentArgs.fromSavedStateHandle(savedStateHandle)

    private val setData = MutableStateFlow<UIState<SetModel>>(UIState.Idle)
    val set = setData.asStateFlow()

    private val deleteData = MutableStateFlow<UIState<Boolean>>(UIState.Idle)
    val delete = deleteData.asStateFlow()

    fun getSetById() {
        viewModelScope.launch {
            setsRepository.getSetById(args.setId).collectLatest { setData.value = it }
        }
    }

    fun getCards(keyword: String): Flow<PagingData<CardModel>> =
        cardsRepository.getCards(args.setId, keyword).cachedIn(viewModelScope)

    fun deleteSet() {
        viewModelScope.launch {
            setsRepository.delete(args.setId).collectLatest { deleteData.value = it }
        }
    }
}