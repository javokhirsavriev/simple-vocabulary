package uz.javokhirdev.svocabulary.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import dagger.hilt.android.AndroidEntryPoint
import uz.javokhirdev.extensions.hideSoftKeyboard
import uz.javokhirdev.svocabulary.R
import uz.javokhirdev.svocabulary.databinding.ActivityHomeBinding

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    private val binding by lazy { ActivityHomeBinding.inflate(layoutInflater) }

    private val navController by lazy {
        (supportFragmentManager.findFragmentById(R.id.hostFragment) as NavHostFragment).navController
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        with(binding) {
            navController.addOnDestinationChangedListener { _, _, _ ->
                hideSoftKeyboard()
            }
        }
    }
}