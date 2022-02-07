package uz.javokhirdev.svocabulary.database

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import uz.javokhirdev.svocabulary.database.sets.SetsDao
import uz.javokhirdev.svocabulary.database.cards.CardsDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {

    @[Singleton Provides]
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room
            .databaseBuilder(context, AppDatabase::class.java, "svocabulary-db")
            .build()
    }

    @[Singleton Provides]
    fun provideSetsDao(appDatabase: AppDatabase): SetsDao = appDatabase.setsDao()

    @[Singleton Provides]
    fun provideCardsDao(appDatabase: AppDatabase): CardsDao = appDatabase.cardsDao()
}