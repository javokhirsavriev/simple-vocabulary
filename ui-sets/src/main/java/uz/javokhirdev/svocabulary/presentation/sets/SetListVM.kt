package uz.javokhirdev.svocabulary.presentation.sets

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import uz.javokhirdev.svocabulary.data.model.SetModel
import uz.javokhirdev.svocabulary.repository.SetsRepository
import javax.inject.Inject

@HiltViewModel
class SetListVM @Inject constructor(
    private val repository: SetsRepository
) : ViewModel() {

    fun getSets(): Flow<PagingData<SetModel>> = repository.getSets().cachedIn(viewModelScope)
}