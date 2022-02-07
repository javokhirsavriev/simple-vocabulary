package uz.javokhirdev.svocabulary.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import uz.javokhirdev.svocabulary.data.db.sets.SetEntity
import uz.javokhirdev.svocabulary.data.db.sets.SetsDao
import uz.javokhirdev.svocabulary.data.db.cards.CardEntity
import uz.javokhirdev.svocabulary.data.db.cards.CardsDao

@Database(
    entities = [
        SetEntity::class,
        CardEntity::class,
    ],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun setsDao(): SetsDao

    abstract fun cardsDao(): CardsDao
}