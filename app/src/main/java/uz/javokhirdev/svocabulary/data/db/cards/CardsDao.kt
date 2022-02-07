package uz.javokhirdev.svocabulary.data.db.cards

import androidx.paging.PagingSource
import androidx.room.*

@Dao
interface CardsDao {

    @Query("SELECT * FROM cards WHERE card_set_id = :setId ORDER BY created_at DESC")
    fun getWords(setId: Long): PagingSource<Int, CardEntity>

    @Query("SELECT * FROM cards WHERE card_id = :id")
    suspend fun getWordById(id: Long): CardEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(obj: CardEntity): Long

    @Update
    suspend fun update(obj: CardEntity)

    @Transaction
    suspend fun insertOrUpdate(obj: CardEntity) {
        val id = insert(obj)
        if (id == -1L) update(obj)
    }

    @Delete
    suspend fun delete(obj: CardEntity)

    @Query("DELETE FROM cards WHERE card_set_id = :setId")
    suspend fun deleteWordsBySetId(setId: Long)
}