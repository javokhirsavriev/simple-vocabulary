package uz.javokhirdev.svocabulary.presentation.setDetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import uz.javokhirdev.svocabulary.data.UIState
import uz.javokhirdev.svocabulary.data.db.sets.SetEntity
import uz.javokhirdev.svocabulary.domain.repository.SetsRepository
import javax.inject.Inject

@HiltViewModel
class SetDetailVM @Inject constructor(
    private val repository: SetsRepository
) : ViewModel() {

    private val createData = MutableStateFlow<UIState<SetEntity>>(UIState.Idle)
    val create = createData.asStateFlow()

    fun createSet(entity: SetEntity) {
        viewModelScope.launch {
            repository.insertOrUpdate(entity).collectLatest { createData.value = it }
        }
    }
}