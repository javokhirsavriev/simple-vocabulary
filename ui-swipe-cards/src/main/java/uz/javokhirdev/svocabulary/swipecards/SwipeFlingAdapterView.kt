package uz.javokhirdev.svocabulary.swipecards

import android.content.Context
import android.database.DataSetObserver
import android.graphics.PointF
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.Adapter
import android.widget.FrameLayout
import uz.javokhirdev.extensions.isNull
import uz.javokhirdev.svocabulary.swipecards.FlingCardListener.FlingListener

/**
 * Created by Savriev Javokhir
 * for package uz.javokhirdev.svocabulary.swipecards
 * and project Simple Vocabulary.
 * Use with caution dinausaurs might appear!
 */
class SwipeFlingAdapterView : BaseFlingAdapterView {

    private var MAX_VISIBLE = 4
    private var MIN_ADAPTER_STACK = 6
    private var ROTATION_DEGREES = 15f
    private var LAST_OBJECT_IN_STACK = 0

    private var mAdapter: Adapter? = null
    private var mDataSetObserver: AdapterDataSetObserver? = null

    private var mOnItemClickListener: OnItemClickListener? = null
    private var mFlingListener: SwipeFlingListener? = null
    private var flingCardListener: FlingCardListener? = null

    private var mActiveCard: View? = null
    private var mLastTouchPoint: PointF? = null

    private var mInLayout = false

    constructor(context: Context?) : super(context)

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        init(attrs, defStyle)
    }

    override fun getSelectedView(): View? = mActiveCard

    override fun requestLayout() {
        if (!mInLayout) super.requestLayout()
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)

        // If we don't have an adapter, we don't need to do anything
        val mAdapter = mAdapter ?: return

        mInLayout = true

        val adapterCount = mAdapter.count

        if (adapterCount == 0) {
            removeAllViewsInLayout()
        } else {
            val topCard = getChildAt(LAST_OBJECT_IN_STACK)

            if (mActiveCard != null && topCard != null && topCard === mActiveCard) {
                flingCardListener?.let {
                    if (it.isTouching) {
                        val lastPoint = it.lastPoint

                        if (mLastTouchPoint == null || mLastTouchPoint != lastPoint) {
                            mLastTouchPoint = lastPoint
                            removeViewsInLayout(0, LAST_OBJECT_IN_STACK)
                            layoutChildren(1, adapterCount)
                        }
                    }
                }
            } else {
                // Reset the UI and set top view listener
                removeAllViewsInLayout()
                layoutChildren(0, adapterCount)
                setTopView()
            }
        }

        mInLayout = false

        if (adapterCount <= MIN_ADAPTER_STACK) mFlingListener?.onAdapterAboutToEmpty(adapterCount)
    }

    private fun init(attrs: AttributeSet?, defStyle: Int) {
        val attr =
            context.obtainStyledAttributes(attrs, R.styleable.SwipeFlingAdapterView, defStyle, 0)

        MAX_VISIBLE =
            attr.getInt(R.styleable.SwipeFlingAdapterView_maxVisible, MAX_VISIBLE)
        MIN_ADAPTER_STACK =
            attr.getInt(R.styleable.SwipeFlingAdapterView_minAdapterStack, MIN_ADAPTER_STACK)
        ROTATION_DEGREES =
            attr.getFloat(R.styleable.SwipeFlingAdapterView_rotationDegrees, ROTATION_DEGREES)

        attr.recycle()
    }

    private fun layoutChildren(startingIndex: Int, adapterCount: Int) {
        var startingIn = startingIndex

        while (startingIn < adapterCount.coerceAtMost(MAX_VISIBLE)) {
            mAdapter?.let {
                val newUnderChild = it.getView(startingIn, null, this)

                if (newUnderChild.visibility != GONE) {
                    makeAndAddView(newUnderChild)
                    LAST_OBJECT_IN_STACK = startingIn
                }

                startingIn++
            }
        }
    }

    private fun makeAndAddView(child: View) {
        val lp = child.layoutParams as FrameLayout.LayoutParams
        addViewInLayout(child, 0, lp, true)

        val needToMeasure = child.isLayoutRequested

        if (needToMeasure) {
            val childWidthSpec = getChildMeasureSpec(
                widthMeasureSpec,
                paddingLeft + paddingRight + lp.leftMargin + lp.rightMargin,
                lp.width
            )
            val childHeightSpec = getChildMeasureSpec(
                heightMeasureSpec,
                paddingTop + paddingBottom + lp.topMargin + lp.bottomMargin,
                lp.height
            )
            child.measure(childWidthSpec, childHeightSpec)
        } else {
            cleanupLayoutState(child)
        }

        val w = child.measuredWidth
        val h = child.measuredHeight

        var gravity = lp.gravity
        if (gravity == -1) gravity = Gravity.TOP or Gravity.START

        val layoutDirection = layoutDirection
        val absoluteGravity = Gravity.getAbsoluteGravity(gravity, layoutDirection)
        val verticalGravity = gravity and Gravity.VERTICAL_GRAVITY_MASK

        val childLeft = when (absoluteGravity and Gravity.HORIZONTAL_GRAVITY_MASK) {
            Gravity.CENTER_HORIZONTAL -> (width + paddingLeft - paddingRight - w) / 2 +
                    lp.leftMargin - lp.rightMargin
            Gravity.END -> width + paddingRight - w - lp.rightMargin
            Gravity.START -> paddingLeft + lp.leftMargin
            else -> paddingLeft + lp.leftMargin
        }

        val childTop = when (verticalGravity) {
            Gravity.CENTER_VERTICAL -> (height + paddingTop - paddingBottom - h) / 2 + lp.topMargin - lp.bottomMargin
            Gravity.BOTTOM -> height - paddingBottom - h - lp.bottomMargin
            Gravity.TOP -> paddingTop + lp.topMargin
            else -> paddingTop + lp.topMargin
        }

        child.layout(childLeft, childTop, childLeft + w, childTop + h)
    }

    /**
     * Set the top view and add the fling listener
     */
    private fun setTopView() {
        if (childCount > 0) {
            mActiveCard = getChildAt(LAST_OBJECT_IN_STACK)
            mActiveCard?.let {
                flingCardListener = FlingCardListener(
                    it,
                    mAdapter?.getItem(0),
                    ROTATION_DEGREES, object : FlingListener {
                        override fun onCardExited() {
                            mActiveCard = null
                            mFlingListener?.removeFirstObjectInAdapter()
                        }

                        override fun leftExit(dataObject: Any?) {
                            mFlingListener?.onLeftCardExit(dataObject)
                        }

                        override fun rightExit(dataObject: Any?) {
                            mFlingListener?.onRightCardExit(dataObject)
                        }

                        override fun onClick(dataObject: Any?) {
                            mOnItemClickListener?.onItemClicked(0, dataObject)
                        }

                        override fun onScroll(scrollProgressPercent: Float) {
                            mFlingListener?.onScroll(scrollProgressPercent)
                        }
                    })
                it.setOnTouchListener(flingCardListener)
            }
        }
    }

    val topCardListener: FlingCardListener?
        get() {
            return flingCardListener
        }

    fun setMaxVisible(MAX_VISIBLE: Int) {
        this.MAX_VISIBLE = MAX_VISIBLE
    }

    fun setMinStackInAdapter(MIN_ADAPTER_STACK: Int) {
        this.MIN_ADAPTER_STACK = MIN_ADAPTER_STACK
    }

    override fun getAdapter(): Adapter? = mAdapter

    override fun setAdapter(adapter: Adapter) {
        if (!mAdapter.isNull() && !mDataSetObserver.isNull()) {
            mAdapter?.unregisterDataSetObserver(mDataSetObserver)
            mDataSetObserver = null
        }

        mAdapter = adapter

        if (!mAdapter.isNull() && mDataSetObserver.isNull()) {
            mDataSetObserver = AdapterDataSetObserver()
            mAdapter?.registerDataSetObserver(mDataSetObserver)
        }
    }

    fun setFlingListener(SwipeFlingListener: SwipeFlingListener?) {
        mFlingListener = SwipeFlingListener
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener?) {
        mOnItemClickListener = onItemClickListener
    }

    override fun generateLayoutParams(attrs: AttributeSet): LayoutParams {
        return FrameLayout.LayoutParams(context, attrs)
    }

    private inner class AdapterDataSetObserver : DataSetObserver() {
        override fun onChanged() {
            requestLayout()
        }

        override fun onInvalidated() {
            requestLayout()
        }
    }

    interface OnItemClickListener {
        fun onItemClicked(itemPosition: Int, dataObject: Any?)
    }

    interface SwipeFlingListener {
        fun removeFirstObjectInAdapter()

        fun onLeftCardExit(dataObject: Any?)

        fun onRightCardExit(dataObject: Any?)

        fun onAdapterAboutToEmpty(itemsInAdapter: Int)

        fun onScroll(scrollProgressPercent: Float)
    }
}