package uz.javokhirdev.svocabulary.presentation.settings

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.kaopiz.kprogresshud.KProgressHUD
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import uz.javokhirdev.extensions.*
import uz.javokhirdev.svocabulary.data.*
import uz.javokhirdev.svocabulary.preferences.AppStoreRepository
import uz.javokhirdev.svocabulary.presentation.components.R
import uz.javokhirdev.svocabulary.presentation.settings.databinding.FragmentSettingsBinding
import uz.javokhirdev.svocabulary.utils.*
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment :
    Fragment(uz.javokhirdev.svocabulary.presentation.settings.R.layout.fragment_settings) {

    private val binding by viewBinding(FragmentSettingsBinding::bind)

    private val viewModel by viewModels<SettingsVM>()

    @Inject
    lateinit var appStore: AppStoreRepository

    private var progressHud: KProgressHUD? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        progressHud = KProgressHUD.create(requireContext())
            .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
            .setCancellable(false)
            .setAnimationSpeed(2)
            .setDimAmount(0.5f)

        with(binding) {
            toolbar.setNavigationOnClickListener { findNavController().navigateUp() }

            textImport.onClick { }
            textExport.onClick { requestStorage.launch(arrayOf(WES, RES)) }
            textContact.onClick { requireContext().sendMail() }
            textReset.onClick { resetProgress() }
            textShare.onClick { requireContext().shareText(APP_URL) }
        }

        with(viewModel) {
            repeatingJobOnStarted { export.collectLatest { onExportState(it) } }
        }

        repeatingJobOnStarted {
            appStore.isDarkMode.collect { setDarkMode(it) }
        }
    }

    private fun onExportState(uiState: UIState<Boolean>) {
        uiState onLoading {
            if (isLoading) progressHud?.show() else progressHud?.dismiss()
        } onSuccess {
            data.orFalse().ifTrue { toast(getString(R.string.csv_file_generated)) }
        } onFailure {
            toast(message)
        }
    }

    private fun resetProgress() {
        requireContext().showDialog(
            title = getString(R.string.reset_title),
            message = getString(R.string.reset_description),
            positiveText = getString(R.string.cancel),
            negativeText = getString(R.string.reset),
            cancelAction = { viewModel.resetProgress() }
        )
    }

    private fun setDarkMode(isDark: Boolean) {
        if (isDark) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        with(binding) {
            val pair = if (isDark) {
                getString(R.string.light_mode) to R.drawable.ic_light_mode
            } else {
                getString(R.string.dark_mode) to R.drawable.ic_dark_mode
            }

            textDarkMode.text = pair.first
            textDarkMode.drawableEnd(pair.second)
            textDarkMode.onClick {
                lifecycleScope.launch { appStore.setIsDarkMode(!isDark) }
            }
        }
    }

    private val requestStorage =
        requestPermissions { if (this) viewModel.exportDatabaseToCSVFile(requireContext()) }
}