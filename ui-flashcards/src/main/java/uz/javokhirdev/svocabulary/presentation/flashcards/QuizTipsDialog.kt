package uz.javokhirdev.svocabulary.presentation.flashcards

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import uz.javokhirdev.extensions.onClick
import uz.javokhirdev.svocabulary.base.BaseSheetFragment
import uz.javokhirdev.svocabulary.presentation.flashcards.databinding.LayoutFlashcardQuizTipsBinding

class QuizTipsDialog : BaseSheetFragment<LayoutFlashcardQuizTipsBinding>() {

    override fun viewBindingInflate(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): LayoutFlashcardQuizTipsBinding =
        LayoutFlashcardQuizTipsBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            buttonOk.onClick { dismissAllowingStateLoss() }
        }
    }
}