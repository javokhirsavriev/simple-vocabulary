package uz.javokhirdev.svocabulary.presentation.flashcards.data

import android.view.LayoutInflater
import android.view.ViewGroup
import uz.javokhirdev.svocabulary.base.BaseSheetFragment
import uz.javokhirdev.svocabulary.presentation.flashcards.databinding.LayoutFlashcardTipsSheetBinding

class TipsSheet : BaseSheetFragment<LayoutFlashcardTipsSheetBinding>() {

    override fun viewBindingInflate(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): LayoutFlashcardTipsSheetBinding =
        LayoutFlashcardTipsSheetBinding.inflate(inflater, container, false)
}