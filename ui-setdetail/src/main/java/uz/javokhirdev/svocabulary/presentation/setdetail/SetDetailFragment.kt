package uz.javokhirdev.svocabulary.presentation.setdetail

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import uz.javokhirdev.extensions.*
import uz.javokhirdev.svocabulary.data.UIState
import uz.javokhirdev.svocabulary.data.model.SetModel
import uz.javokhirdev.svocabulary.data.onLoading
import uz.javokhirdev.svocabulary.data.onSuccess
import uz.javokhirdev.svocabulary.presentation.components.R
import uz.javokhirdev.svocabulary.presentation.components.databinding.FragmentSetDetailBinding

@AndroidEntryPoint
class SetDetailFragment : Fragment(R.layout.fragment_set_detail) {

    private val binding by viewBinding(FragmentSetDetailBinding::bind)

    private val viewModel by viewModels<SetDetailVM>()

    private var title = ""
    private var description = ""
    private var isNewCreate = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            toolbar.setNavigationOnClickListener { findNavController().navigateUp() }

            inputTitle.onTextChangeListener { title = this }
            inputDescription.onTextChangeListener { description = this }

            buttonSave.onClick { createSet() }
        }

        with(viewModel) {
            repeatingJobOnStarted { set.collectLatest { onSetState(it) } }
            repeatingJobOnStarted { create.collectLatest { onCreateState(it) } }
        }
    }

    private fun onSetState(uiState: UIState<SetModel>) {
        with(binding) {
            uiState onLoading {
                buttonSave.onLoading(isLoading)
            } onSuccess {
                setSetData(data)
            }
        }
    }

    private fun onCreateState(uiState: UIState<Boolean>) {
        with(binding) {
            uiState onLoading {
                buttonSave.onLoading(isLoading)
            } onSuccess {
                if (data.orFalse()) findNavController().navigateUp()
            }
        }
    }

    private fun setSetData(obj: SetModel? = null) {
        title = obj?.title.orEmpty()
        description = obj?.description.orEmpty()
        isNewCreate = obj?.id.isNull()

        val toolbarTitle = if (isNewCreate) R.string.create_set else R.string.edit_set
        val buttonText = if (isNewCreate) R.string.save else R.string.edit
        val buttonIcon = if (isNewCreate) R.drawable.ic_save else R.drawable.ic_edit

        with(binding) {
            toolbar.setTitle(toolbarTitle)

            inputTitle.setText(title)
            inputDescription.setText(description)

            buttonSave.setButtonText(getString(buttonText))
            buttonSave.setIconResource(buttonIcon)
        }
    }

    private fun createSet() {
        if (title.trim().isEmpty()) {
            toast(R.string.please_enter_title)
            return
        }

        viewModel.createSet(isNewCreate, title, description)
    }
}