package uz.javokhirdev.svocabulary.utils

import android.content.Context
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import uz.javokhirdev.extensions.EmptyBlock
import uz.javokhirdev.svocabulary.presentation.components.R

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