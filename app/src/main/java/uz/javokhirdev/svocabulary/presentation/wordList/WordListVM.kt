package uz.javokhirdev.svocabulary.presentation.wordList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import uz.javokhirdev.svocabulary.data.UIState
import uz.javokhirdev.svocabulary.data.db.words.WordEntity
import uz.javokhirdev.svocabulary.domain.repository.WordsRepository
import javax.inject.Inject

@HiltViewModel
class WordListVM @Inject constructor(
    private val repository: WordsRepository
) : ViewModel() {

    private val wordsData = MutableStateFlow<UIState<List<WordEntity>>>(UIState.Idle)
    val words = wordsData.asStateFlow()

    init {
        getSets()
    }

    private fun getSets() {
        viewModelScope.launch {
            repository.getWords().collectLatest { wordsData.value = it }
        }
    }
}