package uz.javokhirdev.svocabulary.app

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import uz.javokhirdev.svocabulary.app.App
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @[Singleton Provides]
    fun provideApplication(@ApplicationContext app: Context): App = app as App

    @[Singleton Provides]
    fun provideContext(application: App): Context = application.applicationContext
}