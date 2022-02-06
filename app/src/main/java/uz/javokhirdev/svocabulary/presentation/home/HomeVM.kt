package uz.javokhirdev.svocabulary.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import uz.javokhirdev.svocabulary.data.UIState
import uz.javokhirdev.svocabulary.data.db.sets.SetEntity
import uz.javokhirdev.svocabulary.domain.repository.SetsRepository
import javax.inject.Inject

@AndroidEntryPoint
class HomeVM @Inject constructor(
    private val repository: SetsRepository
) : ViewModel() {

    private val setsData = MutableStateFlow<UIState<List<SetEntity>>>(UIState.Idle)
    val sets = setsData.asStateFlow()

    init {
        getSets()
    }

    private fun getSets() {
        viewModelScope.launch {
            repository.getSets().collectLatest { setsData.value = it }
        }
    }
}