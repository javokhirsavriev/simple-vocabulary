package uz.javokhirdev.svocabulary.utils

import kotlinx.coroutines.Dispatchers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DispatcherProvider @Inject constructor() {

    fun getIO() = Dispatchers.IO

    fun getMain() = Dispatchers.Main

    fun getUnconfined() = Dispatchers.Unconfined

    fun getDefault() = Dispatchers.Default
}