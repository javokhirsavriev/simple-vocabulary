package uz.javokhirdev.svocabulary.presentation.cards

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import uz.javokhirdev.extensions.*
import uz.javokhirdev.svocabulary.base.*
import uz.javokhirdev.svocabulary.data.NOT_ID
import uz.javokhirdev.svocabulary.data.SET_ID
import uz.javokhirdev.svocabulary.data.UIState
import uz.javokhirdev.svocabulary.data.model.CardModel
import uz.javokhirdev.svocabulary.data.onSuccess
import uz.javokhirdev.svocabulary.presentation.cards.databinding.FragmentCardsBinding
import uz.javokhirdev.svocabulary.presentation.components.R
import uz.javokhirdev.svocabulary.presentation.flashcards.FlashcardsActivity
import uz.javokhirdev.svocabulary.utils.TTSManager
import uz.javokhirdev.svocabulary.utils.copy
import uz.javokhirdev.svocabulary.utils.showDialog
import javax.inject.Inject

@AndroidEntryPoint
class CardsFragment :
    Fragment(uz.javokhirdev.svocabulary.presentation.cards.R.layout.fragment_cards),
    CardsAdapter.CardListener, ActionSheet.ActionSheetListener {

    private val binding by viewBinding(FragmentCardsBinding::bind)

    private val viewModel by viewModels<CardsVM>()

    private val args by navArgs<CardsFragmentArgs>()

    @Inject
    lateinit var ttsManager: TTSManager

    private var cardsAdapter: CardsAdapter? = null

    private var selectedCard: CardModel? = null

    private var keywords = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cardsAdapter = CardsAdapter(requireContext(), this)
        cardsAdapter?.addLoadStateListener { onCardsState(it) }

        with(binding) {
            toolbar.title = args.setTitle
            toolbar.setNavigationOnClickListener { findNavController().navigateUp() }
            toolbar.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    uz.javokhirdev.svocabulary.presentation.cards.R.id.addCard -> {
                        navigateToCardDetail()
                        true
                    }
                    uz.javokhirdev.svocabulary.presentation.cards.R.id.clearAll -> {
                        clearAll()
                        true
                    }
                    else -> false
                }
            }

            rvCards.vertical().adapter = cardsAdapter

            inputSearch.setText(keywords)
            inputSearch.onTextChangeListener { getCards(this) }

            buttonFlashcard.onClick { navigateToFlashcards() }
        }

        with(viewModel) {
            repeatingJobOnStarted { delete.collectLatest { onDeleteState(it) } }
            repeatingJobOnStarted { clearAll.collectLatest { onDeleteState(it) } }
        }

        getCards()
    }

    override fun onDestroyView() {
        ttsManager.shutDown()
        super.onDestroyView()
    }

    override fun onCardClick(item: CardModel) {
        item.term?.let { ttsManager.say(it) }
    }

    override fun onCardLongClick(item: CardModel) = showActionSheet(item)

    override fun onSetSheetAction(sheetState: SheetState) {
        selectedCard?.let { card ->
            sheetState onCopy {
                copy("${card.term.orEmpty()}\n${card.definition.orEmpty()}")
            } onListen {
                card.term?.let { ttsManager.say(it) }
            } onEdit {
                card.id?.let { navigateToCardDetail(it) }
            } onDelete {
                card.id?.let { viewModel.deleteCard(it) }
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
                        buttonFlashcard.hide()
                        loadingView.onLoading(true)
                    }
                    is LoadState.NotLoading -> {
                        if (cardsAdapter?.itemCount == 0) {
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
        binding.buttonFlashcard.hide()

        uiState onSuccess {
            data.orFalse().ifTrue { getCards(keywords) }
        }
    }

    private fun getCards(text: String = "") {
        keywords = text

        repeatingJobOnStarted {
            viewModel.getCards(keywords).collectLatest { cardsAdapter?.submitData(it) }
        }
    }

    private fun clearAll() {
        requireContext().showDialog(
            title = getString(R.string.clear_all),
            message = getString(R.string.clear_all_description),
            positiveText = getString(R.string.cancel),
            negativeText = getString(R.string.delete),
            cancelAction = { viewModel.clearAll() }
        )
    }

    private fun navigateToCardDetail(cardId: Long? = NOT_ID) {
        val direction = CardsFragmentDirections.cardsToDetail(
            cardId = cardId ?: NOT_ID,
            setId = args.setId
        )
        findNavController().navigate(direction)
    }

    private fun navigateToFlashcards() {
        val intent = Intent(requireActivity(), FlashcardsActivity::class.java)
        intent.putExtra(SET_ID, args.setId)
        startActivity(intent)
    }

    private fun showActionSheet(item: CardModel) {
        selectedCard = item

        val dialog = ActionSheet.newInstance(this, true)
        childFragmentManager.run { dialog.show(this, dialog.tag) }
    }
}