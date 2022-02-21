package uz.javokhirdev.svocabulary.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import uz.javokhirdev.svocabulary.repository.SetsRepository
import javax.inject.Inject

@HiltViewModel
class SettingsVM @Inject constructor(
    private val setsRepository: SetsRepository
) : ViewModel() {

    fun resetProgress() {
        viewModelScope.launch {
            setsRepository.deleteAll().collect()
        }
    }
}