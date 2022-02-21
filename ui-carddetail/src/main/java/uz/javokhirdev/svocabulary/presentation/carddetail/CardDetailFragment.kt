package uz.javokhirdev.svocabulary.presentation.carddetail

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import uz.javokhirdev.extensions.*
import uz.javokhirdev.svocabulary.data.UIState
import uz.javokhirdev.svocabulary.data.model.CardModel
import uz.javokhirdev.svocabulary.data.onFailure
import uz.javokhirdev.svocabulary.data.onLoading
import uz.javokhirdev.svocabulary.data.onSuccess
import uz.javokhirdev.svocabulary.presentation.carddetail.databinding.FragmentCardDetailBinding
import uz.javokhirdev.svocabulary.presentation.components.R

@AndroidEntryPoint
class CardDetailFragment : Fragment(uz.javokhirdev.svocabulary.presentation.carddetail.R.layout.fragment_card_detail) {

    private val binding by viewBinding(FragmentCardDetailBinding::bind)

    private val viewModel by viewModels<CardDetailVM>()

    private var term = ""
    private var definition = ""
    private var isNewCreate = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            toolbar.setNavigationOnClickListener { findNavController().navigateUp() }

            inputTerm.setText(term)
            inputTerm.onTextChangeListener { term = this }

            inputDefinition.setText(definition)
            inputDefinition.onTextChangeListener { definition = this }

            buttonSave.onClick { createCard() }
        }

        with(viewModel) {
            repeatingJobOnStarted { card.collectLatest { onCardState(it) } }
            repeatingJobOnStarted { create.collectLatest { onCreateState(it) } }
        }
    }

    private fun onCardState(uiState: UIState<CardModel>) {
        with(binding) {
            uiState onLoading {
                buttonSave.onLoading(isLoading)
            } onSuccess {
                setCardData(data)
            }
        }
    }

    private fun onCreateState(uiState: UIState<Boolean>) {
        with(binding) {
            uiState onLoading {
                buttonSave.onLoading(isLoading)
            } onSuccess {
                findNavController().navigateUp()
            } onFailure {
                toast(message)
            }
        }
    }

    private fun setCardData(obj: CardModel? = null) {
        isNewCreate = obj?.id.isNull()

        if (!isNewCreate) {
            term = obj?.term.orEmpty()
            definition = obj?.definition.orEmpty()
        }

        val toolbarTitle = if (isNewCreate) R.string.create_card else R.string.edit_card
        val buttonText = if (isNewCreate) R.string.save else R.string.edit
        val buttonIcon = if (isNewCreate) R.drawable.ic_save else R.drawable.ic_edit

        with(binding) {
            toolbar.setTitle(toolbarTitle)

            if (!isNewCreate) {
                inputTerm.setText(term)
                inputDefinition.setText(definition)
            }

            buttonSave.setButtonText(getString(buttonText))
            buttonSave.setIconResource(buttonIcon)
        }
    }

    private fun createCard() {
        if (term.trim().isEmpty()) {
            toast(R.string.please_enter_term)
            return
        }

        if (definition.trim().isEmpty()) {
            toast(R.string.please_enter_definition)
            return
        }

        viewModel.createCard(isNewCreate, term, definition)
    }
}