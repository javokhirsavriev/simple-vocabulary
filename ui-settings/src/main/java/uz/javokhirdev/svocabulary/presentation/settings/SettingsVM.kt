package uz.javokhirdev.svocabulary.presentation.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import uz.javokhirdev.svocabulary.data.UIState
import uz.javokhirdev.svocabulary.data.model.SetWithCardsModel
import uz.javokhirdev.svocabulary.repository.SetsRepository
import uz.javokhirdev.svocabulary.utils.generateCsvFile
import javax.inject.Inject

@HiltViewModel
class SettingsVM @Inject constructor(
    private val setsRepository: SetsRepository
) : ViewModel() {

    private val exportData = MutableStateFlow<UIState<Boolean>>(UIState.Idle)
    val export = exportData.asStateFlow()

    fun resetProgress() {
        viewModelScope.launch {
            setsRepository.deleteAll().collect()
        }
    }

    fun importDatabaseFromCSVFile() {

    }

    fun exportDatabaseToCSVFile(context: Context) {
        viewModelScope.launch {
            exportData.value = UIState.loading(true)

            setsRepository.getSetsWithCards().collectLatest { list ->
                if (list.isNotEmpty()) {
                    list.forEach { generateFile(context, it) }
                }

                exportData.value = UIState.loading(false)
                exportData.value = UIState.success(list.isNotEmpty())
                exportData.value = UIState.Idle
            }
        }
    }

    private fun generateFile(context: Context, model: SetWithCardsModel) {
        try {
            context.generateCsvFile(model.set.title.orEmpty()) {
                csvWriter().open(this, append = false) {
                    writeRow(listOf("id", "setId", "term", "definition"))

                    model.cards.forEach { card ->
                        writeRow(listOf(card.id, card.setId, card.term, card.definition))
                    }
                }
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }
}