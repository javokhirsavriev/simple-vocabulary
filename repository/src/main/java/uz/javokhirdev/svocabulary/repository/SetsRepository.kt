package uz.javokhirdev.svocabulary.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import kotlinx.coroutines.flow.*
import uz.javokhirdev.svocabulary.data.PAGE_SIZE
import uz.javokhirdev.svocabulary.data.UIState
import uz.javokhirdev.svocabulary.data.model.CardModel
import uz.javokhirdev.svocabulary.data.model.SetModel
import uz.javokhirdev.svocabulary.data.model.SetWithCardsModel
import uz.javokhirdev.svocabulary.database.cards.CardsDao
import uz.javokhirdev.svocabulary.database.sets.SetEntity
import uz.javokhirdev.svocabulary.database.sets.SetsDao
import uz.javokhirdev.svocabulary.utils.DispatcherProvider
import javax.inject.Inject

class SetsRepository @Inject constructor(
    private val setsDao: SetsDao,
    private val cardsDao: CardsDao,
    private val dispatcher: DispatcherProvider
) {

    fun getSets(): Flow<PagingData<SetModel>> {
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                enablePlaceholders = false,
                prefetchDistance = 3
            ),
            pagingSourceFactory = { setsDao.getSets() }
        ).flow.map {
            it.map { entity ->
                SetModel(
                    id = entity.id,
                    title = entity.title,
                    description = entity.description
                )
            }
        }
    }

    suspend fun getSetsWithCards() = flow {
        val entity = setsDao.getSetsWithCards().map {
            SetWithCardsModel(
                set = SetModel(
                    id = it.set.id,
                    title = it.set.title,
                    description = it.set.description
                ),
                cards = it.cards.map { card ->
                    CardModel(
                        id = card.id,
                        setId = card.setId,
                        term = card.term,
                        definition = card.definition
                    )
                }
            )
        }
        emit(entity)
    }.catch {
        emit(emptyList())
    }.flowOn(dispatcher.getIO())

    suspend fun getSetById(id: Long) = flow {
        emit(UIState.loading(true))

        val entity = setsDao.getSetById(id)
        val setModel = SetModel(
            id = entity?.id,
            title = entity?.title,
            description = entity?.description
        )

        emit(UIState.loading(false))
        emit(UIState.success(setModel))
    }.catch { throwable ->
        emit(UIState.loading(false))
        emit(UIState.failure(throwable.message.orEmpty()))
    }.flowOn(dispatcher.getIO())

    suspend fun insertOrUpdate(
        isNewCreate: Boolean,
        setId: Long,
        title: String,
        description: String
    ) = flow {
        emit(UIState.loading(true))

        val entity = if (isNewCreate) {
            SetEntity(
                title = title.trim(),
                description = description.trim()
            )
        } else {
            SetEntity(
                id = setId,
                title = title.trim(),
                description = description.trim()
            )
        }

        setsDao.insertOrUpdate(entity)

        emit(UIState.loading(false))
        emit(UIState.success(true))
    }.catch {
        emit(UIState.loading(false))
        emit(UIState.success(false))
    }.flowOn(dispatcher.getIO())

    suspend fun delete(setId: Long) = flow {
        emit(UIState.loading(true))

        setsDao.delete(setId)
        cardsDao.deleteCardsBySetId(setId)

        emit(UIState.loading(false))
        emit(UIState.success(true))
    }.catch {
        emit(UIState.loading(false))
        emit(UIState.success(false))
    }.flowOn(dispatcher.getIO())

    suspend fun deleteAll() = flow {
        emit(UIState.loading(true))

        setsDao.deleteAll()
        cardsDao.deleteAll()

        emit(UIState.loading(false))
        emit(UIState.success(true))
    }.catch {
        emit(UIState.loading(false))
        emit(UIState.success(false))
    }.flowOn(dispatcher.getIO())
}