package uz.javokhirdev.svocabulary.utils

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.TextToSpeech.LANG_NOT_SUPPORTED
import android.speech.tts.TextToSpeech.OnInitListener
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.*
import javax.inject.Inject

class TTSManager @Inject constructor(@ApplicationContext context: Context?) {

    private var tts: TextToSpeech? = null
    private var isLoaded = false
    private val locale = Locale.US

    init {
        try {
            val onInitListener = OnInitListener { status ->
                if (status == TextToSpeech.SUCCESS) {
                    tts?.language = locale
                    isLoaded = true
                }
            }
            tts = TextToSpeech(context, onInitListener)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    fun say(text: String) {
        if (isLoaded) {
            val available = tts?.isLanguageAvailable(locale) ?: LANG_NOT_SUPPORTED

            if (available >= TextToSpeech.LANG_AVAILABLE) {
                tts?.language = locale
                tts?.setSpeechRate(0.85f)
                tts?.speak(
                    text,
                    TextToSpeech.QUEUE_FLUSH,
                    null,
                    "random string - 2d22332"
                )
            }
        }
    }

    fun shutDown() {
        tts?.shutdown()
    }
}