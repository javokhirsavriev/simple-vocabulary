package uz.javokhirdev.svocabulary.domain.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import uz.javokhirdev.svocabulary.data.UIState
import uz.javokhirdev.svocabulary.data.db.sets.SetEntity
import uz.javokhirdev.svocabulary.data.db.sets.SetsDao
import uz.javokhirdev.svocabulary.data.db.cards.CardsDao
import uz.javokhirdev.svocabulary.utils.DispatcherProvider
import uz.javokhirdev.svocabulary.utils.PAGE_SIZE
import javax.inject.Inject

class SetsRepository @Inject constructor(
    private val setsDao: SetsDao,
    private val cardsDao: CardsDao,
    private val dispatcher: DispatcherProvider
) {

    fun getSets(): Flow<PagingData<SetEntity>> {
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                enablePlaceholders = false,
                prefetchDistance = 3
            ),
            pagingSourceFactory = { setsDao.getSets() }
        ).flow
    }

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

    suspend fun delete(setId: Long) = flow {
        emit(UIState.loading(true))

        setsDao.delete(setId)
        cardsDao.deleteWordsBySetId(setId)

        emit(UIState.loading(false))
        emit(UIState.success(setId))
    }.catch { throwable ->
        emit(UIState.loading(false))
        emit(UIState.failure(throwable.message.orEmpty()))
    }.flowOn(dispatcher.getIO())
}