package uz.javokhirdev.svocabulary.domain.repository

import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import uz.javokhirdev.svocabulary.data.UIState
import uz.javokhirdev.svocabulary.data.db.sets.SetEntity
import uz.javokhirdev.svocabulary.data.db.sets.SetsDao
import uz.javokhirdev.svocabulary.data.db.words.WordsDao
import uz.javokhirdev.svocabulary.utils.DispatcherProvider
import javax.inject.Inject

class SetsRepository @Inject constructor(
    private val setsDao: SetsDao,
    private val wordsDao: WordsDao,
    private val dispatcher: DispatcherProvider
) {

    suspend fun getSets() = flow {
        emit(UIState.loading(true))

        val sets = setsDao.getSets()

        emit(UIState.loading(false))
        emit(UIState.success(sets))
    }.catch { throwable ->
        emit(UIState.loading(false))
        emit(UIState.failure(throwable.message.orEmpty()))
    }.flowOn(dispatcher.getIO())

    suspend fun getSetById(id: Long) = flow {
        emit(UIState.loading(true))

        val set = setsDao.getSetById(id)

        emit(UIState.loading(false))
        emit(UIState.success(set))
    }.catch { throwable ->
        emit(UIState.loading(false))
        emit(UIState.failure(throwable.message.orEmpty()))
    }.flowOn(dispatcher.getIO())

    suspend fun insertOrUpdate(obj: SetEntity) = flow {
        emit(UIState.loading(true))

        setsDao.insertOrUpdate(obj)

        emit(UIState.loading(false))
        emit(UIState.success(obj))
    }.catch { throwable ->
        emit(UIState.loading(false))
        emit(UIState.failure(throwable.message.orEmpty()))
    }.flowOn(dispatcher.getIO())

    suspend fun delete(obj: SetEntity) = flow {
        emit(UIState.loading(true))

        setsDao.delete(obj)
        wordsDao.deleteWordsBySetId(obj.id)

        emit(UIState.loading(false))
        emit(UIState.success(obj))
    }.catch { throwable ->
        emit(UIState.loading(false))
        emit(UIState.failure(throwable.message.orEmpty()))
    }.flowOn(dispatcher.getIO())
}