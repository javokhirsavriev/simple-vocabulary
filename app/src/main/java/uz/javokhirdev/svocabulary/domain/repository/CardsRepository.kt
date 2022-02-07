package uz.javokhirdev.svocabulary.domain.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import uz.javokhirdev.svocabulary.data.UIState
import uz.javokhirdev.svocabulary.data.db.cards.CardEntity
import uz.javokhirdev.svocabulary.data.db.cards.CardsDao
import uz.javokhirdev.svocabulary.utils.DispatcherProvider
import uz.javokhirdev.svocabulary.utils.PAGE_SIZE
import javax.inject.Inject

class CardsRepository @Inject constructor(
    private val cardsDao: CardsDao,
    private val dispatcher: DispatcherProvider
) {

    fun getCards(setId: Long): Flow<PagingData<CardEntity>> {
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                enablePlaceholders = false,
                prefetchDistance = 3
            ),
            pagingSourceFactory = { cardsDao.getWords(setId) }
        ).flow
    }

    suspend fun getCardById(id: Long) = flow {
        emit(UIState.loading(true))

        val set = cardsDao.getWordById(id)

        emit(UIState.loading(false))
        emit(UIState.success(set))
    }.catch { throwable ->
        emit(UIState.loading(false))
        emit(UIState.failure(throwable.message.orEmpty()))
    }.flowOn(dispatcher.getIO())

    suspend fun insertOrUpdate(obj: CardEntity) = flow {
        emit(UIState.loading(true))

        cardsDao.insertOrUpdate(obj)

        emit(UIState.loading(false))
        emit(UIState.success(obj))
    }.catch { throwable ->
        emit(UIState.loading(false))
        emit(UIState.failure(throwable.message.orEmpty()))
    }.flowOn(dispatcher.getIO())

    suspend fun delete(obj: CardEntity) = flow {
        emit(UIState.loading(true))

        cardsDao.delete(obj)

        emit(UIState.loading(false))
        emit(UIState.success(obj))
    }.catch { throwable ->
        emit(UIState.loading(false))
        emit(UIState.failure(throwable.message.orEmpty()))
    }.flowOn(dispatcher.getIO())
}