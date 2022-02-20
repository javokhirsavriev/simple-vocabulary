package uz.javokhirdev.svocabulary.database.cards

import androidx.paging.PagingSource
import androidx.room.*

@Dao
interface CardsDao {

    @Query(
        "SELECT * FROM cards WHERE card_set_id = :setId " +
                "AND (card_term LIKE :keywords OR card_definition LIKE :keywords)" +
                "ORDER BY created_at DESC"
    )
    fun getCards(setId: Long, keywords: String): PagingSource<Int, CardEntity>

    @Query("SELECT * FROM cards WHERE card_set_id = :setId")
    fun getCards(setId: Long): List<CardEntity>

    @Query("SELECT * FROM cards WHERE card_id = :id")
    suspend fun getCardById(id: Long): CardEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(obj: CardEntity): Long

    @Update
    suspend fun update(obj: CardEntity)

    @Transaction
    suspend fun insertOrUpdate(obj: CardEntity) {
        val id = insert(obj)
        if (id == -1L) update(obj)
    }

    @Query("DELETE FROM cards WHERE card_id = :cardId")
    suspend fun delete(cardId: Long)

    @Query("DELETE FROM cards WHERE card_set_id = :setId")
    suspend fun deleteCardsBySetId(setId: Long)
}