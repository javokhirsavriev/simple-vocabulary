package uz.javokhirdev.svocabulary.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import uz.javokhirdev.svocabulary.data.db.sets.SetEntity
import uz.javokhirdev.svocabulary.domain.repository.SetsRepository
import javax.inject.Inject

@HiltViewModel
class HomeVM @Inject constructor(
    private val repository: SetsRepository
) : ViewModel() {

    fun getSets(): Flow<PagingData<SetEntity>> = repository.getSets().cachedIn(viewModelScope)
}