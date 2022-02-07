package uz.javokhirdev.svocabulary.presentation.flashcards

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import uz.javokhirdev.svocabulary.presentation.flashcards.databinding.ActivityFlashcardsBinding

@AndroidEntryPoint
class FlashcardsActivity : AppCompatActivity() {

    private val binding by lazy { ActivityFlashcardsBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        with(binding) {

        }
    }
}