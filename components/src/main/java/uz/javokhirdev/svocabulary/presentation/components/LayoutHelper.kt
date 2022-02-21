package uz.javokhirdev.svocabulary.presentation.components

import android.content.Context
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.RelativeLayout
import uz.javokhirdev.extensions.dpToPx
import uz.javokhirdev.svocabulary.data.WRAP_CONTENT

object LayoutHelper {

    private fun getSize(context: Context, size: Int): Int {
        return if (size < 0) size else context.dpToPx(size)
    }

    /* ViewGroup */
    fun createViewGroup(
        context: Context,
        width: Int = WRAP_CONTENT,
        height: Int = WRAP_CONTENT
    ): ViewGroup.LayoutParams {
        return ViewGroup.LayoutParams(
            getSize(context, width),
            getSize(context, height)
        )
    }

    /* ViewGroup */
    fun createFrame(
        context: Context,
        width: Int = WRAP_CONTENT,
        height: Int = WRAP_CONTENT
    ): FrameLayout.LayoutParams {
        return FrameLayout.LayoutParams(
            getSize(context, width),
            getSize(context, height)
        )
    }

    /* LinearLayout */
    fun createLinear(
        context: Context,
        width: Int = WRAP_CONTENT,
        height: Int = WRAP_CONTENT
    ): LinearLayout.LayoutParams {
        return LinearLayout.LayoutParams(
            getSize(context, width),
            getSize(context, height)
        )
    }

    fun createLinear(
        context: Context,
        width: Int = WRAP_CONTENT,
        height: Int = WRAP_CONTENT,
        leftMargin: Int = 0,
        topMargin: Int = 0,
        rightMargin: Int = 0,
        bottomMargin: Int = 0
    ): LinearLayout.LayoutParams {
        val layoutParams = LinearLayout.LayoutParams(
            getSize(context, width),
            getSize(context, height)
        )
        layoutParams.setMargins(
            context.dpToPx(leftMargin),
            context.dpToPx(topMargin),
            context.dpToPx(rightMargin),
            context.dpToPx(bottomMargin)
        )
        return layoutParams
    }

    fun createLinear(
        context: Context,
        width: Int,
        height: Int,
        weight: Float
    ): LinearLayout.LayoutParams {
        return LinearLayout.LayoutParams(
            getSize(context, width),
            getSize(context, height),
            weight
        )
    }

    /* RelativeLayout */
    fun createRelative(
        context: Context,
        width: Int = WRAP_CONTENT,
        height: Int = WRAP_CONTENT
    ): RelativeLayout.LayoutParams {
        return RelativeLayout.LayoutParams(
            getSize(context, width),
            getSize(context, height)
        )
    }
}