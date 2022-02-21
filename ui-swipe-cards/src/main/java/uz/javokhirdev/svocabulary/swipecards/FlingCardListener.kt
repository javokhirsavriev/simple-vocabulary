package uz.javokhirdev.svocabulary.swipecards

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.graphics.PointF
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.OvershootInterpolator
import kotlin.math.abs
import kotlin.math.cos

/**
 * Created by Savriev Javokhir
 * for package uz.javokhirdev.svocabulary.swipecards
 * and project Simple Vocabulary.
 * Use with caution dinausaurs might appear!
 */
class FlingCardListener(
    frame: View,
    itemAtPosition: Any?,
    rotation_degrees: Float,
    flingListener: FlingListener
) : OnTouchListener {

    private val listener: FlingListener

    private val objectX: Float
    private val objectY: Float
    private val objectH: Int
    private val objectW: Int
    private val parentWidth: Int
    private val dataObject: Any?
    private val halfWidth: Float
    private var BASE_ROTATION_DEGREES: Float

    private var aPosX = 0f
    private var aPosY = 0f
    private var aDownTouchX = 0f
    private var aDownTouchY = 0f

    // The active pointer is the one currently moving our object.
    private var mActivePointerId = INVALID_POINTER_ID
    private var frame: View? = null

    private val TOUCH_ABOVE = 0
    private val TOUCH_BELOW = 1
    private var touchPosition = 0
    private var isAnimationRunning = false
    private val MAX_COS = cos(Math.toRadians(45.0)).toFloat()

    companion object {
        private const val INVALID_POINTER_ID = -1
    }

    init {
        this.frame = frame

        objectX = frame.x
        objectY = frame.y
        objectH = frame.height
        objectW = frame.width
        halfWidth = objectW / 2f
        dataObject = itemAtPosition
        parentWidth = (frame.parent as ViewGroup).width
        BASE_ROTATION_DEGREES = rotation_degrees

        listener = flingListener
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(view: View, event: MotionEvent): Boolean {
        when (event.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> {
                // From http://android-developers.blogspot.com/2010/06/making-sense-of-multitouch.html
                // Save the ID of this pointer
                mActivePointerId = event.getPointerId(0)
                var x = 0f
                var y = 0f
                var success = false

                try {
                    x = event.getX(mActivePointerId)
                    y = event.getY(mActivePointerId)
                    success = true
                } catch (e: IllegalArgumentException) {
                    e.printStackTrace()
                }

                if (success) {
                    // Remember where we started
                    aDownTouchX = x
                    aDownTouchY = y

                    // To prevent an initial jump of the magnifier, aposX and aPosY must
                    // have the values from the magnifier frame
                    frame?.let {
                        if (aPosX == 0f) aPosX = it.x
                        if (aPosY == 0f) aPosY = it.y
                    }

                    touchPosition = if (y < objectH / 2) TOUCH_ABOVE else TOUCH_BELOW
                }
                view.parent.requestDisallowInterceptTouchEvent(true)
            }
            MotionEvent.ACTION_UP -> {
                mActivePointerId = INVALID_POINTER_ID
                resetCardViewOnStack()
                view.parent.requestDisallowInterceptTouchEvent(false)
            }
            MotionEvent.ACTION_POINTER_DOWN -> {}
            MotionEvent.ACTION_POINTER_UP -> {
                // Extract the index of the pointer that left the touch sensor
                val pointerIndex = event.action and
                        MotionEvent.ACTION_POINTER_INDEX_MASK shr MotionEvent.ACTION_POINTER_INDEX_SHIFT
                val pointerId = event.getPointerId(pointerIndex)
                if (pointerId == mActivePointerId) {
                    // This was our active pointer going up. Choose a new
                    // active pointer and adjust accordingly.
                    val newPointerIndex = if (pointerIndex == 0) 1 else 0
                    mActivePointerId = event.getPointerId(newPointerIndex)
                }
            }
            MotionEvent.ACTION_MOVE -> {
                // Find the index of the active pointer and fetch its position
                val pointerIndexMove = event.findPointerIndex(mActivePointerId)
                val xMove = event.getX(pointerIndexMove)
                val yMove = event.getY(pointerIndexMove)

                // From http://android-developers.blogspot.com/2010/06/making-sense-of-multitouch.html
                // Calculate the distance moved
                val dx = xMove - aDownTouchX
                val dy = yMove - aDownTouchY

                // Move the frame
                aPosX += dx
                aPosY += dy

                // Calculate the rotation degrees
                val distobjectX = aPosX - objectX
                var rotation = BASE_ROTATION_DEGREES * 2f * distobjectX / parentWidth

                if (touchPosition == TOUCH_BELOW) rotation = -rotation

                // In this area would be code for doing something with the view as the frame moves.
                frame?.x = aPosX
                frame?.y = aPosY
                frame?.rotation = rotation

                listener.onScroll(scrollProgressPercent)
            }
            MotionEvent.ACTION_CANCEL -> {
                mActivePointerId = INVALID_POINTER_ID
                view.parent.requestDisallowInterceptTouchEvent(false)
            }
        }
        return true
    }

    private val scrollProgressPercent: Float
        get() = when {
            movedBeyondLeftBorder() -> -1f
            movedBeyondRightBorder() -> 1f
            else -> {
                val zeroToOneValue =
                    (aPosX + halfWidth - leftBorder()) / (rightBorder() - leftBorder())
                zeroToOneValue * 2f - 1f
            }
        }

    private fun resetCardViewOnStack(): Boolean {
        if (movedBeyondLeftBorder()) {
            // Left Swipe
            onSelected(true, getExitPoint(-objectW), 100)
            listener.onScroll(-1.0f)
        } else if (movedBeyondRightBorder()) {
            // Right Swipe
            onSelected(false, getExitPoint(parentWidth), 100)
            listener.onScroll(1.0f)
        } else {
            val abslMoveDistance = abs(aPosX - objectX)

            aPosX = 0f
            aPosY = 0f
            aDownTouchX = 0f
            aDownTouchY = 0f

            frame?.animate()
                ?.setDuration(200)
                ?.setInterpolator(OvershootInterpolator(1.5f))
                ?.x(objectX)
                ?.y(objectY)
                ?.rotation(0f)

            listener.onScroll(0.0f)

            if (abslMoveDistance < 4.0) listener.onClick(dataObject)
        }
        return false
    }

    private fun movedBeyondLeftBorder(): Boolean = aPosX + halfWidth < leftBorder()

    private fun movedBeyondRightBorder(): Boolean = aPosX + halfWidth > rightBorder()

    private fun leftBorder(): Float = parentWidth / 4f

    private fun rightBorder(): Float = 3 * parentWidth / 4f

    private fun onSelected(
        isLeft: Boolean,
        exitY: Float, duration: Long
    ) {
        isAnimationRunning = true

        val exitX = if (isLeft) {
            -objectW - rotationWidthOffset
        } else {
            parentWidth + rotationWidthOffset
        }

        frame?.animate()
            ?.setDuration(duration)
            ?.setInterpolator(AccelerateInterpolator())
            ?.x(exitX)
            ?.y(exitY)
            ?.setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    if (isLeft) {
                        listener.onCardExited()
                        listener.leftExit(dataObject)
                    } else {
                        listener.onCardExited()
                        listener.rightExit(dataObject)
                    }
                    isAnimationRunning = false
                }
            })
            ?.rotation(getExitRotation(isLeft))
    }

    /**
     * Starts a default left exit animation.
     */
    fun selectLeft() {
        if (!isAnimationRunning) onSelected(true, objectY, 200)
    }

    /**
     * Starts a default right exit animation.
     */
    fun selectRight() {
        if (!isAnimationRunning) onSelected(false, objectY, 200)
    }

    private fun getExitPoint(exitXPoint: Int): Float {
        val x = FloatArray(2)
        x[0] = objectX
        x[1] = aPosX

        val y = FloatArray(2)
        y[0] = objectY
        y[1] = aPosY

        val regression = LinearRegression(x, y)

        // Your typical y = ax + b linear regression
        return regression.slope().toFloat() * exitXPoint + regression.intercept().toFloat()
    }

    private fun getExitRotation(isLeft: Boolean): Float {
        var rotation = BASE_ROTATION_DEGREES * 2f * (parentWidth - objectX) / parentWidth

        if (touchPosition == TOUCH_BELOW) rotation = -rotation
        if (isLeft) rotation = -rotation

        return rotation
    }

    /**
     * When the object rotates it's width becomes bigger.
     * The maximum width is at 45 degrees.
     *
     *
     * The below method calculates the width offset of the rotation.
     */
    private val rotationWidthOffset: Float
        get() = objectW / MAX_COS - objectW

    fun setRotationDegrees(degrees: Float) {
        BASE_ROTATION_DEGREES = degrees
    }

    val isTouching: Boolean
        get() = mActivePointerId != INVALID_POINTER_ID

    val lastPoint: PointF
        get() = PointF(aPosX, aPosY)

    interface FlingListener {
        fun onCardExited()

        fun leftExit(dataObject: Any?)

        fun rightExit(dataObject: Any?)

        fun onClick(dataObject: Any?)

        fun onScroll(scrollProgressPercent: Float)
    }
}