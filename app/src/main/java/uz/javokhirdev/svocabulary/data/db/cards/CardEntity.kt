package uz.javokhirdev.svocabulary.data.db.cards

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cards")
data class CardEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "card_id")
    val id: Long = 1L,

    @ColumnInfo(name = "card_set_id")
    val setId: Long? = null,

    @ColumnInfo(name = "card_term")
    val term: String? = null,

    @ColumnInfo(name = "card_definition")
    val definition: String? = null,

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
)