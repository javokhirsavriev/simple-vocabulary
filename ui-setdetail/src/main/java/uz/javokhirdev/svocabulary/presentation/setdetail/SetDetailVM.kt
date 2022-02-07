package uz.javokhirdev.svocabulary.presentation.setdetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import uz.javokhirdev.svocabulary.data.UIState
import uz.javokhirdev.svocabulary.data.model.SetModel
import uz.javokhirdev.svocabulary.repository.SetsRepository
import javax.inject.Inject

@HiltViewModel
class SetDetailVM @Inject constructor(
    private val repository: SetsRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val args = SetDetailFragmentArgs.fromSavedStateHandle(savedStateHandle)

    private val setData = MutableStateFlow<UIState<SetModel>>(UIState.Idle)
    val set = setData.asStateFlow()

    private val createData = MutableStateFlow<UIState<Boolean>>(UIState.Idle)
    val create = createData.asStateFlow()

    init {
        getSetById()
    }

    private fun getSetById() {
        viewModelScope.launch {
            repository.getSetById(args.setId).collectLatest { setData.value = it }
        }
    }

    fun createSet(isNewCreate: Boolean, title: String, description: String) {
        viewModelScope.launch {
            repository.insertOrUpdate(
                isNewCreate = isNewCreate,
                setId = args.setId,
                title = title,
                description = description
            ).collectLatest { createData.value = it }
        }
    }
}