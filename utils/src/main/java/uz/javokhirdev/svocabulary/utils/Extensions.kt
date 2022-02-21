package uz.javokhirdev.svocabulary.utils

import android.content.Context
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import uz.javokhirdev.extensions.EmptyBlock
import uz.javokhirdev.extensions.copyToClipboard
import uz.javokhirdev.extensions.toast
import uz.javokhirdev.svocabulary.presentation.components.R

fun Drawable.colorFilter(color: Int) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        colorFilter = BlendModeColorFilter(color, BlendMode.SRC_IN)
    } else {
        setColorFilter(color, PorterDuff.Mode.SRC_IN)
    }
}

fun Context.showDialog(
    title: String,
    message: String,
    positiveText: String = getString(R.string.add_card),
    negativeText: String = getString(R.string.cancel),
    okAction: EmptyBlock = {},
    cancelAction: EmptyBlock = {}
) {
    MaterialAlertDialogBuilder(this, R.style.DialogStyle)
        .setTitle(title)
        .setMessage(message)
        .setPositiveButton(positiveText) { _, _ -> okAction.invoke() }
        .setNegativeButton(negativeText) { _, _ -> cancelAction.invoke() }
        .show()
}

fun Fragment.copy(text: String) {
    requireContext().copyToClipboard(text)
    toast(getString(R.string.copied))
}