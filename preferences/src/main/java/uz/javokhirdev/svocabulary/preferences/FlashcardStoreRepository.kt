package uz.javokhirdev.svocabulary.preferences

//import android.content.Context
//import androidx.datastore.preferences.core.edit
//import androidx.datastore.preferences.core.emptyPreferences
//import androidx.datastore.preferences.preferencesDataStore
//import dagger.hilt.android.qualifiers.ApplicationContext
//import kotlinx.coroutines.flow.Flow
//import kotlinx.coroutines.flow.catch
//import kotlinx.coroutines.flow.map
//import java.io.IOException
//import javax.inject.Inject
//
//class FlashcardStoreRepository @Inject constructor(@ApplicationContext context: Context) {
//
//    private val dataStore = context.dataStore
//
//    val isFlashcardStarted: Flow<Boolean> = dataStore.data
//        .catch { exception ->
//            if (exception is IOException) {
//                emit(emptyPreferences())
//            } else {
//                throw exception
//            }
//        }.map { preferences ->
//            preferences[PreferencesKeys.IS_FLASHCARD_STARTED] ?: false
//        }
//
//    suspend fun setFlashcardStarted(type: Boolean = false) {
//        dataStore.edit {
//            it[PreferencesKeys.IS_FLASHCARD_STARTED] = type
//        }
//    }
//
//    companion object {
//        private val Context.dataStore by preferencesDataStore(name = "flashcards.store")
//    }
//}