package uz.javokhirdev.svocabulary.domain.repository

import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import uz.javokhirdev.svocabulary.data.UIState
import uz.javokhirdev.svocabulary.data.db.words.WordEntity
import uz.javokhirdev.svocabulary.data.db.words.WordsDao
import uz.javokhirdev.svocabulary.utils.DispatcherProvider
import javax.inject.Inject

class WordsRepository @Inject constructor(
    private val wordsDao: WordsDao,
    private val dispatcher: DispatcherProvider
) {

    suspend fun getWords() = flow {
        emit(UIState.loading(true))

        val sets = wordsDao.getWords()

        emit(UIState.loading(false))
        emit(UIState.success(sets))
    }.catch { throwable ->
        emit(UIState.loading(false))
        emit(UIState.failure(throwable.message.orEmpty()))
    }.flowOn(dispatcher.getIO())

    suspend fun getWordById(id: Long) = flow {
        emit(UIState.loading(true))

        val set = wordsDao.getWordById(id)

        emit(UIState.loading(false))
        emit(UIState.success(set))
    }.catch { throwable ->
        emit(UIState.loading(false))
        emit(UIState.failure(throwable.message.orEmpty()))
    }.flowOn(dispatcher.getIO())

    suspend fun insertOrUpdate(obj: WordEntity) = flow {
        emit(UIState.loading(true))

        wordsDao.insertOrUpdate(obj)

        emit(UIState.loading(false))
        emit(UIState.success(obj))
    }.catch { throwable ->
        emit(UIState.loading(false))
        emit(UIState.failure(throwable.message.orEmpty()))
    }.flowOn(dispatcher.getIO())

    suspend fun delete(obj: WordEntity) = flow {
        emit(UIState.loading(true))

        wordsDao.delete(obj)

        emit(UIState.loading(false))
        emit(UIState.success(obj))
    }.catch { throwable ->
        emit(UIState.loading(false))
        emit(UIState.failure(throwable.message.orEmpty()))
    }.flowOn(dispatcher.getIO())
}