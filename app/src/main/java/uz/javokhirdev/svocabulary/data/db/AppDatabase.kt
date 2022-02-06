package uz.javokhirdev.svocabulary.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import uz.javokhirdev.svocabulary.data.db.sets.SetEntity
import uz.javokhirdev.svocabulary.data.db.sets.SetsDao
import uz.javokhirdev.svocabulary.data.db.words.WordEntity
import uz.javokhirdev.svocabulary.data.db.words.WordsDao

@Database(
    entities = [
        SetEntity::class,
        WordEntity::class,
    ],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun setsDao(): SetsDao

    abstract fun wordsDao(): WordsDao
}