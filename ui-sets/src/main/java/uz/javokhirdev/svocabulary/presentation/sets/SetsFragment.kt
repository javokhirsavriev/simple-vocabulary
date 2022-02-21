package uz.javokhirdev.svocabulary.presentation.sets

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import uz.javokhirdev.extensions.*
import uz.javokhirdev.svocabulary.base.*
import uz.javokhirdev.svocabulary.data.NOT_ID
import uz.javokhirdev.svocabulary.data.UIState
import uz.javokhirdev.svocabulary.data.model.SetModel
import uz.javokhirdev.svocabulary.data.onSuccess
import uz.javokhirdev.svocabulary.presentation.components.R
import uz.javokhirdev.svocabulary.presentation.sets.databinding.FragmentSetsBinding
import uz.javokhirdev.svocabulary.utils.copy

@AndroidEntryPoint
class SetsFragment : Fragment(uz.javokhirdev.svocabulary.presentation.sets.R.layout.fragment_sets),
    SetsAdapter.SetListener, ActionSheet.ActionSheetListener {

    private val binding by viewBinding(FragmentSetsBinding::bind)

    private val viewModel by viewModels<SetsVM>()

    private var setsAdapter: SetsAdapter? = null

    private var selectedSet: SetModel? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setsAdapter = SetsAdapter(requireContext(), this)
        setsAdapter?.addLoadStateListener { onSetsState(it) }

        with(binding) {
            toolbar.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    uz.javokhirdev.svocabulary.presentation.sets.R.id.settings -> {
                        navigateToSettings()
                        true
                    }
                    else -> false
                }
            }

            rvSets.vertical().adapter = setsAdapter

            buttonAdd.onClick { navigateToSetDetail() }
        }

        with(viewModel) {
            repeatingJobOnStarted { delete.collectLatest { onDeleteState(it) } }
        }

        getSets()
    }

    override fun onSetClick(item: SetModel) = navigateToWordList(item)

    override fun onSetLongClick(item: SetModel) = showActionSheet(item)

    override fun onSetSheetAction(sheetState: SheetState) {
        selectedSet?.let { set ->
            sheetState onCopy {
                copy("${set.title.orEmpty()}\n${set.description.orEmpty()}")
            } onEdit {
                set.id?.let { navigateToSetDetail(it) }
            } onDelete {
                set.id?.let { viewModel.deleteSet(it) }
            }
        }
    }

    private fun onSetsState(state: CombinedLoadStates) {
        try {
            val loadState = state.source.refresh

            with(binding) {
                when (loadState) {
                    is LoadState.Loading -> {
                        rvSets.beGone()
                        loadingView.onLoading(true)
                    }
                    is LoadState.NotLoading -> {
                        if (setsAdapter?.itemCount == 0) {
                            loadingView.onFailure(getString(R.string.no_data))
                        } else {
                            rvSets.beVisible()
                            loadingView.onLoading(false)
                        }
                    }
                    is LoadState.Error -> {
                        loadingView.onFailure(getString(R.string.no_data))
                        rvSets.beGone()
                    }
                }
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    private fun onDeleteState(uiState: UIState<Boolean>) {
        uiState onSuccess {
            data.orFalse().ifTrue { getSets() }
        }
    }

    private fun getSets() {
        repeatingJobOnStarted {
            viewModel.getSets().collectLatest { setsAdapter?.submitData(it) }
        }
    }

    private fun navigateToSetDetail(setId: Long = NOT_ID) {
        val direction = SetsFragmentDirections.setsToDetail(setId = setId)
        findNavController().navigate(direction)
    }

    private fun navigateToWordList(item: SetModel) {
        val direction = SetsFragmentDirections.setsToCards(
            setId = item.id ?: NOT_ID,
            setTitle = item.title ?: getString(R.string.cards)
        )
        findNavController().navigate(direction)
    }

    private fun navigateToSettings() {
        val direction = SetsFragmentDirections.setsToSettings()
        findNavController().navigate(direction)
    }

    private fun showActionSheet(item: SetModel) {
        selectedSet = item

        val dialog = ActionSheet.newInstance(this)
        childFragmentManager.run { dialog.show(this, dialog.tag) }
    }
}