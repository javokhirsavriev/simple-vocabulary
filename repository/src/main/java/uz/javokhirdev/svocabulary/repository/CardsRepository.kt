package uz.javokhirdev.svocabulary.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import kotlinx.coroutines.flow.*
import uz.javokhirdev.svocabulary.data.PAGE_SIZE
import uz.javokhirdev.svocabulary.data.UIState
import uz.javokhirdev.svocabulary.data.model.CardModel
import uz.javokhirdev.svocabulary.database.cards.CardEntity
import uz.javokhirdev.svocabulary.database.cards.CardsDao
import uz.javokhirdev.svocabulary.utils.DispatcherProvider
import javax.inject.Inject

class CardsRepository @Inject constructor(
    private val cardsDao: CardsDao,
    private val dispatcher: DispatcherProvider
) {

    fun getCards(setId: Long, keyword: String): Flow<PagingData<CardModel>> {
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                enablePlaceholders = false,
                prefetchDistance = 3
            ),
            pagingSourceFactory = {
                cardsDao.getCards(
                    setId = setId,
                    keyword = "%$keyword%"
                )
            }
        ).flow.map {
            it.map { entity ->
                CardModel(
                    id = entity.id,
                    setId = entity.setId,
                    term = entity.term,
                    definition = entity.definition
                )
            }
        }
    }

    suspend fun getCards(id: Long) = flow {
        emit(UIState.loading(true))

        val entity = cardsDao
            .getCards(id)
            .map { entity ->
                CardModel(
                    id = entity.id,
                    setId = entity.setId,
                    term = entity.term,
                    definition = entity.definition
                )
            }
            .shuffled()

        emit(UIState.loading(false))
        emit(UIState.success(entity))
    }.catch { throwable ->
        emit(UIState.loading(false))
        emit(UIState.failure(throwable.message.orEmpty()))
    }.flowOn(dispatcher.getIO())

    suspend fun getCardById(id: Long) = flow {
        emit(UIState.loading(true))

        val entity = cardsDao.getCardById(id)
        val cardModel = CardModel(
            id = entity?.id,
            setId = entity?.setId,
            term = entity?.term,
            definition = entity?.definition
        )

        emit(UIState.loading(false))
        emit(UIState.success(cardModel))
    }.catch { throwable ->
        emit(UIState.loading(false))
        emit(UIState.failure(throwable.message.orEmpty()))
    }.flowOn(dispatcher.getIO())

    suspend fun insertOrUpdate(
        isNewCreate: Boolean,
        cardId: Long,
        setId: Long,
        term: String,
        definition: String
    ) = flow {
        emit(UIState.loading(true))

        val entity = if (isNewCreate) {
            CardEntity(
                term = term.trim(),
                setId = setId,
                definition = definition.trim()
            )
        } else {
            CardEntity(
                id = cardId,
                setId = setId,
                term = term.trim(),
                definition = definition.trim()
            )
        }

        cardsDao.insertOrUpdate(entity)

        emit(UIState.loading(false))
        emit(UIState.success(true))
    }.catch {
        emit(UIState.loading(false))
        emit(UIState.success(false))
    }.flowOn(dispatcher.getIO())

    suspend fun delete(cardId: Long) = flow {
        emit(UIState.loading(true))

        cardsDao.delete(cardId)

        emit(UIState.loading(false))
        emit(UIState.success(true))
    }.catch {
        emit(UIState.loading(false))
        emit(UIState.success(false))
    }.flowOn(dispatcher.getIO())
}