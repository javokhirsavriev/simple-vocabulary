package uz.javokhirdev.svocabulary.data.db.words

import androidx.room.*

@Dao
interface WordsDao {

    @Query("SELECT * FROM words ORDER BY created_at DESC")
    suspend fun getWords(): List<WordEntity>

    @Query("SELECT * FROM words WHERE word_id = :id")
    suspend fun getWordById(id: Long): WordEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(obj: WordEntity): Long

    @Update
    suspend fun update(obj: WordEntity)

    @Transaction
    suspend fun insertOrUpdate(obj: WordEntity) {
        val id = insert(obj)
        if (id == -1L) update(obj)
    }

    @Delete
    suspend fun delete(obj: WordEntity)

    @Query("DELETE FROM words WHERE word_set_id = :setId")
    suspend fun deleteWordsBySetId(setId: Long)
}