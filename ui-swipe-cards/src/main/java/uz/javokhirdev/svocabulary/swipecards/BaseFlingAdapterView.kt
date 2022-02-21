package uz.javokhirdev.svocabulary.swipecards

import android.content.Context
import android.util.AttributeSet
import android.widget.Adapter
import android.widget.AdapterView

/**
 * Created by Savriev Javokhir
 * for package uz.javokhirdev.svocabulary.swipecards
 * and project Simple Vocabulary.
 * Use with caution dinausaurs might appear!
 */
abstract class BaseFlingAdapterView : AdapterView<Adapter> {

    var heightMeasureSpec = 0
        private set
    var widthMeasureSpec = 0
        private set

    constructor(context: Context?) : super(context)

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    )

    override fun setSelection(i: Int) {
        throw UnsupportedOperationException("Not supported")
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        this.widthMeasureSpec = widthMeasureSpec
        this.heightMeasureSpec = heightMeasureSpec
    }
}