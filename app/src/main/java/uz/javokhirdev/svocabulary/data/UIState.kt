package uz.javokhirdev.svocabulary.data

sealed class UIState<out T> where T : Any? {
    object Idle : UIState<Nothing>()
    data class Loading(val isLoading: Boolean) : UIState<Nothing>()
    data class Success<T>(val data: T? = null) : UIState<T>()
    data class Failure(val message: String) : UIState<Nothing>()

    companion object {
        fun loading(isLoading: Boolean) = Loading(isLoading)
        fun <T> success(data: T? = null) = Success(data)
        fun failure(message: String) = Failure(message)
    }
}