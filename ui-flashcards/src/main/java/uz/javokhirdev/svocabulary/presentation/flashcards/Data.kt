package uz.javokhirdev.svocabulary.presentation.flashcards

data class CountData(
    val current: Int = 0,
    val all: Int = 0,
    val correct: Int = 0,
    val incorrect: Int = 0
)

data class FinishedData(
    val isFinished: Boolean = false,
    val all: Int = 0,
    val correct: Int = 0,
    val incorrect: Int = 0
)