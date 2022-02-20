package uz.javokhirdev.svocabulary.presentation.sets

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
import uz.javokhirdev.svocabulary.data.model.SetModel
import uz.javokhirdev.svocabulary.repository.SetsRepository
import javax.inject.Inject

@HiltViewModel
class SetsVM @Inject constructor(
    private val repository: SetsRepository
) : ViewModel() {

    private val deleteData = MutableStateFlow<UIState<Boolean>>(UIState.Idle)
    val delete = deleteData.asStateFlow()

    fun getSets(): Flow<PagingData<SetModel>> = repository.getSets().cachedIn(viewModelScope)

    fun deleteSet(setId: Long) {
        viewModelScope.launch {
            repository.delete(setId).collectLatest { deleteData.value = it }
        }
    }
}