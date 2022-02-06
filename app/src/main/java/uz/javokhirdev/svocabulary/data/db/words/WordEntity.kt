package uz.javokhirdev.svocabulary.data.db.words

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "words")
data class WordEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "word_id")
    val id: Long = 1L,

    @ColumnInfo(name = "word_set_id")
    val setId: Long? = null,

    @ColumnInfo(name = "word_title")
    val title: String? = null,

    @ColumnInfo(name = "word_translation")
    val translation: String? = null,

    @ColumnInfo(name = "word_description")
    val description: String? = null,

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
)