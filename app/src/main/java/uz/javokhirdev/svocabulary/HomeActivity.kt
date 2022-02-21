package uz.javokhirdev.svocabulary

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.fragment.NavHostFragment
import dagger.hilt.android.AndroidEntryPoint
import uz.javokhirdev.extensions.hideSoftKeyboard
import uz.javokhirdev.extensions.repeatingJobOnStarted
import uz.javokhirdev.svocabulary.databinding.ActivityHomeBinding
import uz.javokhirdev.svocabulary.preferences.AppStoreRepository
import javax.inject.Inject

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    private val binding by lazy { ActivityHomeBinding.inflate(layoutInflater) }

    private val navController by lazy {
        (supportFragmentManager.findFragmentById(R.id.hostFragment) as NavHostFragment).navController
    }

    @Inject
    lateinit var appStore: AppStoreRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        navController.addOnDestinationChangedListener { _, _, _ ->
            hideSoftKeyboard()
        }

        repeatingJobOnStarted {
            appStore.isDarkMode.collect { setDarkMode(it) }
        }
    }

    private fun setDarkMode(isDark: Boolean) {
        if (isDark) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }
}