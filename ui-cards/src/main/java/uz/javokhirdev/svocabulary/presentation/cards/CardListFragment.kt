package uz.javokhirdev.svocabulary.presentation.cards

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
import uz.javokhirdev.svocabulary.data.UIState
import uz.javokhirdev.svocabulary.data.model.CardModel
import uz.javokhirdev.svocabulary.data.model.SetModel
import uz.javokhirdev.svocabulary.data.onSuccess
import uz.javokhirdev.svocabulary.presentation.components.R
import uz.javokhirdev.svocabulary.presentation.components.databinding.FragmentCardListBinding
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
                    R.id.addCard -> {
                        navigateToCardDetail()
                        true
                    }
                    R.id.editSet -> {
                        navigateToSetDetail()
                        true
                    }
                    R.id.deleteSet -> {
                        deleteSet()
                        true
                    }
                    else -> false
                }
            }

            rvCards.vertical().adapter = cardListAdapter

            inputSearch.onTextChangeListener { getCards(this) }

            buttonFlashcard.onClick { }
        }

        with(viewModel) {
            getSetById()

            repeatingJobOnStarted { set.collectLatest { onSetState(it) } }
            repeatingJobOnStarted { delete.collectLatest { onDeleteState(it) } }
        }

        getCards()
    }

    override fun onDestroyView() {
        ttsManager.shutDown()
        super.onDestroyView()
    }

    override fun onCardClick(item: CardModel) {
        navigateToCardDetail(item.id)
    }

    override fun onVolumeClick(item: CardModel) {
        item.term?.let { ttsManager.say(it) }
    }

    private fun onSetState(uiState: UIState<SetModel>) {
        uiState onSuccess {
            setId = data?.id ?: NOT_ID

            binding.toolbar.title = data?.title ?: getString(R.string.cards)
        }
    }

    private fun onCardsState(state: CombinedLoadStates) {
        try {
            val loadState = state.source.refresh

            with(binding) {
                when (loadState) {
                    is LoadState.Loading -> {
                        rvCards.beGone()
                        buttonFlashcard.hide()
                        loadingView.onLoading(true)
                    }
                    is LoadState.NotLoading -> {
                        if (cardListAdapter?.itemCount == 0) {
                            loadingView.onFailure()
                        } else {
                            rvCards.beVisible()
                            buttonFlashcard.show()
                            loadingView.onLoading(false)
                        }
                    }
                    is LoadState.Error -> {
                        loadingView.onFailure()
                        rvCards.beGone()
                        buttonFlashcard.hide()
                    }
                }
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    private fun onDeleteState(uiState: UIState<Boolean>) {
        uiState onSuccess {
            data.orFalse().ifTrue { findNavController().navigateUp() }
        }
    }

    private fun getCards(keyword: String = "") {
        with(viewModel) {
            repeatingJobOnStarted {
                getCards(keyword).collectLatest { cardListAdapter?.submitData(it) }
            }
        }
    }

    private fun deleteSet() {
        requireContext().showDialog(
            title = getString(R.string.delete_set),
            message = getString(R.string.delete_set_description),
            positiveText = getString(R.string.cancel),
            negativeText = getString(R.string.delete),
            cancelAction = { viewModel.deleteSet() }
        )
    }

    private fun navigateToCardDetail(cardId: Long? = NOT_ID) {
        val direction = CardListFragmentDirections.cardListToDetail(
            cardId = cardId ?: NOT_ID,
            setId = setId
        )
        findNavController().navigate(direction)
    }

    private fun navigateToSetDetail() {
        val direction = CardListFragmentDirections.cardListToSetDetail(setId = setId)
        findNavController().navigate(direction)
    }
}