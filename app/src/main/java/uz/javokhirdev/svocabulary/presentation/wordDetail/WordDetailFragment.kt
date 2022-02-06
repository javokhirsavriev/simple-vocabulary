package uz.javokhirdev.svocabulary.presentation.wordDetail

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import uz.javokhirdev.svocabulary.R

class WordDetailFragment : Fragment() {

    companion object {
        fun newInstance() = WordDetailFragment()
    }

    private lateinit var viewModel: WordDetailVM

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_word_detail, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(WordDetailVM::class.java)
        // TODO: Use the ViewModel
    }

}