package uz.javokhirdev.svocabulary.presentation.cardList

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
import uz.javokhirdev.svocabulary.data.UIState
import uz.javokhirdev.svocabulary.data.db.sets.SetEntity
import uz.javokhirdev.svocabulary.data.db.cards.CardEntity
import uz.javokhirdev.svocabulary.data.onFailure
import uz.javokhirdev.svocabulary.data.onSuccess
import uz.javokhirdev.svocabulary.databinding.FragmentCardListBinding
import uz.javokhirdev.svocabulary.presentation.adapters.CardListAdapter
import uz.javokhirdev.svocabulary.presentation.home.HomeFragmentDirections
import uz.javokhirdev.svocabulary.utils.NOT_ID
import uz.javokhirdev.svocabulary.utils.TTSManager
import uz.javokhirdev.svocabulary.utils.showDialog
import javax.inject.Inject

@AndroidEntryPoint
class CardListFragment : Fragment(R.layout.fragment_card_list), CardListAdapter.CardListener {

    private val binding by viewBinding(FragmentCardListBinding::bind)

    private val viewModel by viewModels<CardListVM>()

    @Inject
    lateinit var ttsManager: TTSManager

    private var cardListAdapter: CardListAdapter? = null

    private var setId = NOT_ID

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cardListAdapter = CardListAdapter(requireContext(), this)
        cardListAdapter?.addLoadStateListener { onCardsState(it) }

        with(binding) {
            toolbar.setNavigationOnClickListener { findNavController().navigateUp() }
            toolbar.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.editSet -> {
                        deleteOrEditSet(true)
                        true
                    }
                    R.id.deleteSet -> {
                        deleteOrEditSet(false)
                        true
                    }
                    else -> false
                }
            }

            buttonAdd.onClick { navigateToCardDetail() }
        }

        with(viewModel) {
            getSetById()

            repeatingJobOnStarted { set.collectLatest { onSetState(it) } }
            repeatingJobOnStarted { getCards().collectLatest { cardListAdapter?.submitData(it) } }
            repeatingJobOnStarted { delete.collectLatest { onDeleteState(it) } }
        }
    }

    override fun onDestroyView() {
        ttsManager.shutDown()
        super.onDestroyView()
    }

    override fun onCardClick(item: CardEntity) {
        navigateToCardDetail(item.id)
    }

    override fun onVolumeClick(item: CardEntity) {
        item.term?.let { ttsManager.say(it) }
    }

    private fun onSetState(uiState: UIState<SetEntity>) {
        with(binding) {
            uiState onSuccess {
                setId = data?.id ?: NOT_ID

                toolbar.title = data?.title ?: getString(R.string.cards)
            }
        }
    }

    private fun onCardsState(state: CombinedLoadStates) {
        try {
            val loadState = state.source.refresh

            with(binding) {
                when (loadState) {
                    is LoadState.Loading -> {
                        rvCards.beGone()
                        loadingView.onLoading(true)
                    }
                    is LoadState.NotLoading -> {
                        if (cardListAdapter?.itemCount == 0) {
                            loadingView.onFailure(getString(R.string.no_data))
                        } else {
                            rvCards.beVisible()
                            loadingView.onLoading(false)
                        }
                    }
                    is LoadState.Error -> {
                        loadingView.onFailure(getString(R.string.no_data))
                        rvCards.beGone()
                    }
                }
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    private fun onDeleteState(uiState: UIState<Long>) {
        uiState onSuccess {
            findNavController().navigateUp()
        } onFailure {
            toast(message)
        }
    }

    private fun deleteOrEditSet(isEditable: Boolean) {
        when (setId != NOT_ID) {
            true -> isEditable
                .ifTrue {
                    val direction = HomeFragmentDirections.homeToSetDetail(setId = setId)
                    findNavController().navigate(direction)
                }
                .ifFalse {
                    requireContext().showDialog(
                        title = getString(R.string.delete_set),
                        message = getString(R.string.delete_set_description),
                        positiveText = getString(R.string.cancel),
                        negativeText = getString(R.string.delete),
                        cancelAction = { viewModel.deleteSet() }
                    )
                }
            else -> {}
        }
    }

    private fun navigateToCardDetail(cardId: Long = NOT_ID) {
        val direction = CardListFragmentDirections.cardListToDetail(
            cardId = cardId,
            setId = setId
        )
        findNavController().navigate(direction)
    }
}