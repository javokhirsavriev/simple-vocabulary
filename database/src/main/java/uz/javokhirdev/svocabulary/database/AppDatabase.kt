package uz.javokhirdev.svocabulary.database

import androidx.room.Database
import androidx.room.RoomDatabase
import uz.javokhirdev.svocabulary.database.sets.SetEntity
import uz.javokhirdev.svocabulary.database.sets.SetsDao
import uz.javokhirdev.svocabulary.database.cards.CardEntity
import uz.javokhirdev.svocabulary.database.cards.CardsDao

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