package uz.javokhirdev.svocabulary.presentation.components

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.widget.RelativeLayout
import android.widget.TextView
import com.google.android.material.progressindicator.CircularProgressIndicator
import uz.javokhirdev.extensions.color
import uz.javokhirdev.extensions.dpToPx
import uz.javokhirdev.extensions.textColor
import uz.javokhirdev.svocabulary.data.MATCH_PARENT

class LoadingView : RelativeLayout {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    fun onLoading(isLoading: Boolean) {
        removeAllViews()

        if (isLoading) {
            val params = LayoutHelper.createRelative(context)
            params.addRule(CENTER_IN_PARENT, TRUE)

            val indicator = CircularProgressIndicator(context).apply {
                layoutParams = params
                isIndeterminate = true
                indicatorSize = context.dpToPx(48)
                setIndicatorColor(context.color(R.color.colorApp))
                trackCornerRadius = context.dpToPx(96)
                trackThickness = context.dpToPx(4)
            }

            addView(indicator)
        }
    }

    fun onFailure(error: String = context.getString(R.string.no_data)) {
        removeAllViews()

        val params = LayoutHelper.createRelative(context, MATCH_PARENT)
        params.addRule(CENTER_IN_PARENT, TRUE)

        val textView = TextView(context).apply {
            layoutParams = params
            gravity = Gravity.CENTER
            isAllCaps = false
            includeFontPadding = false
            setPadding(
                context.dpToPx(20),
                context.dpToPx(20),
                context.dpToPx(20),
                context.dpToPx(20)
            )
            text = error
            textColor(R.color.colorBlack)
            textSize = 18f
        }

        addView(textView)
    }
}