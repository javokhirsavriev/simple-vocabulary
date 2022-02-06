package uz.javokhirdev.svocabulary.presentation.setDetail

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import uz.javokhirdev.extensions.*
import uz.javokhirdev.svocabulary.R
import uz.javokhirdev.svocabulary.data.UIState
import uz.javokhirdev.svocabulary.data.db.sets.SetEntity
import uz.javokhirdev.svocabulary.data.onFailure
import uz.javokhirdev.svocabulary.data.onLoading
import uz.javokhirdev.svocabulary.data.onSuccess
import uz.javokhirdev.svocabulary.databinding.FragmentSetDetailBinding

@AndroidEntryPoint
class SetDetailFragment : Fragment(R.layout.fragment_set_detail) {

    private val args by navArgs<SetDetailFragmentArgs>()

    private val binding by viewBinding(FragmentSetDetailBinding::bind)

    private val viewModel by viewModels<SetDetailVM>()

    private var title = ""
    private var description = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolbarTitle = if (args.isNewCreate) R.string.create_set else R.string.edit_set
        val buttonText = if (args.isNewCreate) R.string.save else R.string.edit
        val buttonIcon = if (args.isNewCreate) R.drawable.ic_save else R.drawable.ic_edit

        title = args.title
        description = args.description

        with(binding) {
            toolbar.setTitle(toolbarTitle)
            toolbar.setNavigationOnClickListener { findNavController().navigateUp() }

            inputTitle.setText(title)
            inputTitle.onTextChangeListener { title = this }

            inputDescription.setText(description)
            inputDescription.onTextChangeListener { description = this }

            buttonSave.setButtonText(getString(buttonText))
            buttonSave.setIconResource(buttonIcon)
            buttonSave.onClick { createSet() }
        }

        repeatingJobOnStarted {
            viewModel.create.collectLatest { onCreateState(it) }
        }
    }

    private fun onCreateState(uiState: UIState<SetEntity>) {
        with(binding) {
            uiState onLoading {
                buttonSave.onLoading(isLoading)
            } onSuccess {
                toast(R.string.successfully_created)
                findNavController().navigateUp()
            } onFailure {
                toast(message)
            }
        }
    }

    private fun createSet() {
        if (title.trim().isEmpty()) {
            toast(R.string.please_enter_title)
            return
        }

        if (description.trim().isEmpty()) {
            toast(R.string.please_enter_description)
            return
        }

        val entity = if (args.isNewCreate) {
            SetEntity(
                title = title.trim(),
                description = description.trim()
            )
        } else {
            SetEntity(
                id = args.id,
                title = title.trim(),
                description = description.trim()
            )
        }

        viewModel.createSet(entity)
    }
}