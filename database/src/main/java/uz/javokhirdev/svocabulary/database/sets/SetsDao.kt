package uz.javokhirdev.svocabulary.database.sets

import androidx.paging.PagingSource
import androidx.room.*

@Dao
interface SetsDao {

    @Query("SELECT * FROM sets ORDER BY created_at DESC")
    fun getSets(): PagingSource<Int, SetEntity>

    @Transaction
    @Query("SELECT * FROM sets")
    fun getSetsWithCards(): List<SetWithCards>

    @Query("SELECT * FROM sets WHERE set_id = :id")
    suspend fun getSetById(id: Long): SetEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(obj: SetEntity): Long

    @Update
    suspend fun update(obj: SetEntity)

    @Transaction
    suspend fun insertOrUpdate(obj: SetEntity) {
        val id = insert(obj)
        if (id == -1L) update(obj)
    }

    @Query("DELETE FROM sets WHERE set_id = :setId")
    suspend fun delete(setId: Long)

    @Query("DELETE FROM sets")
    suspend fun deleteAll()
}