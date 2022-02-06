package uz.javokhirdev.svocabulary.data.db.sets

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sets")
data class SetEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "set_id")
    val id: Long = 0L,

    @ColumnInfo(name = "set_title")
    val title: String? = null,

    @ColumnInfo(name = "set_description")
    val description: String? = null,

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
)