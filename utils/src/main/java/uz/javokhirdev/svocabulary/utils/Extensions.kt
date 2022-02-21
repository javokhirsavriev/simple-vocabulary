package uz.javokhirdev.svocabulary.utils

import android.content.Context
import android.content.Intent
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import uz.javokhirdev.extensions.EmptyBlock
import uz.javokhirdev.extensions.copyToClipboard
import uz.javokhirdev.extensions.toast
import uz.javokhirdev.svocabulary.data.MY_MAIL
import uz.javokhirdev.svocabulary.presentation.components.R

fun TextView.drawableEnd(@DrawableRes drawable: Int) {
    setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, drawable, 0)
}

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

fun Context.sendMail() {
    try {
        val intent = Intent(
            Intent.ACTION_SENDTO, Uri.fromParts("mailto", MY_MAIL, null)
        )
        intent.putExtra(Intent.EXTRA_SUBJECT, resources.getString(R.string.app_name))
        intent.putExtra(Intent.EXTRA_TEXT, resources.getString(R.string.contact_developer))
        startActivity(Intent.createChooser(intent, "Send mail"))
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun Fragment.copy(text: String) {
    requireContext().copyToClipboard(text)
    toast(getString(R.string.copied))
}