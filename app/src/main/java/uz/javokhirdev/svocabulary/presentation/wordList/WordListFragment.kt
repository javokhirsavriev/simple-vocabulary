package uz.javokhirdev.svocabulary.presentation.wordList

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dagger.hilt.android.AndroidEntryPoint
import uz.javokhirdev.svocabulary.R

@AndroidEntryPoint
class WordListFragment : Fragment() {

    companion object {
        fun newInstance() = WordListFragment()
    }

    private lateinit var viewModel: WordListVM

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_word_list, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(WordListVM::class.java)
        // TODO: Use the ViewModel
    }

}