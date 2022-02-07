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
import uz.javokhirdev.svocabulary.data.NOT_ID
import uz.javokhirdev.svocabulary.data.model.SetModel
import uz.javokhirdev.svocabulary.presentation.components.R
import uz.javokhirdev.svocabulary.presentation.components.databinding.FragmentSetListBinding

@AndroidEntryPoint
class SetListFragment : Fragment(R.layout.fragment_set_list), SetListAdapter.SetListener {

    private val binding by viewBinding(FragmentSetListBinding::bind)

    private val viewModel by viewModels<SetListVM>()

    private var setListAdapter: SetListAdapter? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setListAdapter = SetListAdapter(requireContext(), this)
        setListAdapter?.addLoadStateListener { onSetsState(it) }

        with(binding) {
            toolbar.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.createSet -> {
                        navigateToSetDetail()
                        true
                    }
                    else -> false
                }
            }

            rvSets.vertical().adapter = setListAdapter
        }

        with(viewModel) {
            repeatingJobOnStarted {
                getSets().collectLatest { setListAdapter?.submitData(it) }
            }
        }
    }

    override fun onSetClick(item: SetModel) {
        navigateToWordList(item)
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
                        if (setListAdapter?.itemCount == 0) {
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

    private fun navigateToSetDetail() {
        val direction = SetListFragmentDirections.setListToDetail(setId = NOT_ID)
        findNavController().navigate(direction)
    }

    private fun navigateToWordList(item: SetModel) {
        val direction = SetListFragmentDirections.setListToCardList(setId = item.id ?: NOT_ID)
        findNavController().navigate(direction)
    }
}