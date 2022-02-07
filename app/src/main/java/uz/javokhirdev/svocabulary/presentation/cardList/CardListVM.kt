package uz.javokhirdev.svocabulary.presentation.cardList

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
import uz.javokhirdev.svocabulary.data.db.cards.CardEntity
import uz.javokhirdev.svocabulary.data.db.sets.SetEntity
import uz.javokhirdev.svocabulary.domain.repository.CardsRepository
import uz.javokhirdev.svocabulary.domain.repository.SetsRepository
import javax.inject.Inject

@HiltViewModel
class CardListVM @Inject constructor(
    private val cardsRepository: CardsRepository,
    private val setsRepository: SetsRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val args = CardListFragmentArgs.fromSavedStateHandle(savedStateHandle)

    private val setData = MutableStateFlow<UIState<SetEntity>>(UIState.Idle)
    val set = setData.asStateFlow()

    private val deleteData = MutableStateFlow<UIState<Long>>(UIState.Idle)
    val delete = deleteData.asStateFlow()

    fun getSetById() {
        viewModelScope.launch {
            setsRepository.getSetById(args.setId).collectLatest { setData.value = it }
        }
    }

    fun getCards(keyword: String): Flow<PagingData<CardEntity>> =
        cardsRepository.getCards(args.setId, keyword).cachedIn(viewModelScope)

    fun deleteSet() {
        viewModelScope.launch {
            setsRepository.delete(args.setId).collectLatest { deleteData.value = it }
        }
    }
}