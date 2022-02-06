package uz.javokhirdev.svocabulary.data.db.sets

import androidx.room.*

@Dao
interface SetsDao {

    @Transaction
    @Query("SELECT * FROM sets ORDER BY created_at DESC")
    suspend fun getSets(): List<SetEntity>

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

    @Delete
    suspend fun delete(obj: SetEntity)
}