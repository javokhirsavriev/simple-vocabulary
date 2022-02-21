package uz.javokhirdev.svocabulary.preferences

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

class AppStoreRepository @Inject constructor(@ApplicationContext private val context: Context) {

    private val dataStore = context.dataStore

    val isDarkMode: Flow<Boolean> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map { preferences ->
            preferences[PreferencesKeys.IS_DARK_MODE] ?: false
        }

    suspend fun setIsDarkMode(mode: Boolean) {
        dataStore.edit { it[PreferencesKeys.IS_DARK_MODE] = mode }
    }

    companion object {
        private val Context.dataStore by preferencesDataStore(name = "app.store")
    }
}