package uz.javokhirdev.svocabulary.presentation.setDetail

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import uz.javokhirdev.svocabulary.R

class SetDetailFragment : Fragment() {

    companion object {
        fun newInstance() = SetDetailFragment()
    }

    private lateinit var viewModel: SetDetailVM

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_set_detail, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(SetDetailVM::class.java)
        // TODO: Use the ViewModel
    }

}