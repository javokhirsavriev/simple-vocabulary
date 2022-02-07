package uz.javokhirdev.svocabulary.presentation.home

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
import uz.javokhirdev.svocabulary.R
import uz.javokhirdev.svocabulary.data.db.sets.SetEntity
import uz.javokhirdev.svocabulary.databinding.FragmentHomeBinding
import uz.javokhirdev.svocabulary.presentation.adapters.SetListAdapter
import uz.javokhirdev.svocabulary.utils.NOT_ID

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home), SetListAdapter.SetListener {

    private val binding by viewBinding(FragmentHomeBinding::bind)

    private val viewModel by viewModels<HomeVM>()

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

    override fun onSetClick(item: SetEntity) {
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
        val direction = HomeFragmentDirections.homeToSetDetail(setId = NOT_ID)
        findNavController().navigate(direction)
    }

    private fun navigateToWordList(item: SetEntity) {
        val direction = HomeFragmentDirections.homeToCardList(setId = item.id)
        findNavController().navigate(direction)
    }
}