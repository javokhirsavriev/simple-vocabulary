package uz.javokhirdev.svocabulary.presentation.components

import android.content.Context
import android.util.AttributeSet
import com.google.android.material.button.MaterialButton
import uz.javokhirdev.extensions.beEnableIf
import uz.javokhirdev.svocabulary.R

class IndicatorButton : MaterialButton {

    private var buttonText = ""

    constructor(context: Context) : super(context) {
        initView(context, null)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initView(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initView(context, attrs)
    }

    private fun initView(context: Context, attrs: AttributeSet?) {
        val attributes = context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.IndicatorButton, 0, 0
        )

        buttonText = attributes.getString(R.styleable.IndicatorButton_buttonText) ?: ""

        setButtonText(buttonText)
    }

    fun setButtonText(str: String) {
        buttonText = str
        text = buttonText
    }

    fun onLoading(isLoading: Boolean) {
        beEnableIf(!isLoading)

        val str = if (isLoading) context.getString(R.string.loading) else buttonText
        text = str
    }
}