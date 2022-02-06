package uz.javokhirdev.svocabulary.presentation.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import uz.javokhirdev.extensions.onScrollListener
import uz.javokhirdev.extensions.repeatingJobOnStarted
import uz.javokhirdev.extensions.vertical
import uz.javokhirdev.extensions.viewBinding
import uz.javokhirdev.svocabulary.R
import uz.javokhirdev.svocabulary.data.UIState
import uz.javokhirdev.svocabulary.data.db.sets.SetEntity
import uz.javokhirdev.svocabulary.data.onFailure
import uz.javokhirdev.svocabulary.data.onLoading
import uz.javokhirdev.svocabulary.data.onSuccess
import uz.javokhirdev.svocabulary.databinding.FragmentHomeBinding

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {

    private val binding by viewBinding(FragmentHomeBinding::bind)

    private val viewModel by viewModels<HomeVM>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            rvSets.vertical().adapter
            rvSets.onScrollListener { topAppBar.isSelected = this }
        }

        repeatingJobOnStarted {
            viewModel.sets.collectLatest { onSetsState(it) }
        }
    }

    private fun onSetsState(uiState: UIState<List<SetEntity>>) {
        uiState onLoading {

        } onSuccess {

        } onFailure {

        }
    }
}