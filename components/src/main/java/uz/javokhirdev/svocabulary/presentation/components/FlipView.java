package uz.javokhirdev.svocabulary.presentation.components;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

public class FlipView extends FrameLayout {

    public static final int DEFAULT_FLIP_DURATION = 400;
    public static final int DEFAULT_AUTO_FLIP_BACK_TIME = 1000;

    public enum FlipState {
        FRONT_SIDE, BACK_SIDE
    }

    private AnimatorSet mSetRightOut;
    private AnimatorSet mSetLeftIn;
    private AnimatorSet mSetTopOut;
    private AnimatorSet mSetBottomIn;

    private Context context;
    private View mCardFrontLayout;
    private View mCardBackLayout;

    private String flipType = "vertical";
    private String flipTypeFrom = "right";
    private int flipDuration;
    private int autoFlipBackTime;
    private boolean flipOnTouch;
    private boolean flipEnabled;
    private boolean flipOnceEnabled;
    private boolean autoFlipBack;
    private float x1;
    private float y1;

    private FlipState mFlipState = FlipState.FRONT_SIDE;

    private OnFlipAnimationListener onFlipListener = null;

    public FlipView(Context context) {
        super(context);
        this.context = context;
        init(context, null);
    }

    public FlipView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        // Setting Default Values
        flipOnTouch = true;
        flipDuration = DEFAULT_FLIP_DURATION;
        flipEnabled = true;
        flipOnceEnabled = false;
        autoFlipBack = false;
        autoFlipBackTime = DEFAULT_AUTO_FLIP_BACK_TIME;

        if (attrs != null) {
            final TypedArray attrArray =
                    context.obtainStyledAttributes(attrs, R.styleable.easy_flip_view, 0, 0);
            try {
                flipOnTouch = attrArray.getBoolean(R.styleable.easy_flip_view_flipOnTouch, true);
                flipDuration = attrArray.getInt(R.styleable.easy_flip_view_flipDuration, DEFAULT_FLIP_DURATION);
                flipEnabled = attrArray.getBoolean(R.styleable.easy_flip_view_flipEnabled, true);
                flipOnceEnabled = attrArray.getBoolean(R.styleable.easy_flip_view_flipOnceEnabled, false);
                autoFlipBack = attrArray.getBoolean(R.styleable.easy_flip_view_autoFlipBack, false);
                autoFlipBackTime = attrArray.getInt(R.styleable.easy_flip_view_autoFlipBackTime, DEFAULT_AUTO_FLIP_BACK_TIME);
                flipType = attrArray.getString(R.styleable.easy_flip_view_flipType);
                flipTypeFrom = attrArray.getString(R.styleable.easy_flip_view_flipFrom);

                if (TextUtils.isEmpty(flipType)) {
                    flipType = "vertical";
                }

                if (TextUtils.isEmpty(flipTypeFrom)) {
                    flipTypeFrom = "left";
                }
            } finally {
                attrArray.recycle();
            }
        }

        loadAnimations();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        if (getChildCount() > 2) {
            throw new IllegalStateException("EasyFlipView can host only two direct children!");
        }

        findViews();
        changeCameraDistance();
        setupInitializations();
    }

    @Override
    public void addView(View v, int pos, ViewGroup.LayoutParams params) {
        if (getChildCount() == 2) {
            throw new IllegalStateException("EasyFlipView can host only two direct children!");
        }

        super.addView(v, pos, params);

        findViews();
        changeCameraDistance();
    }

    @Override
    public void removeView(View v) {
        super.removeView(v);

        findViews();
    }

    @Override
    public void removeAllViewsInLayout() {
        super.removeAllViewsInLayout();

        // Reset the state
        mFlipState = FlipState.FRONT_SIDE;

        findViews();
    }

    private void findViews() {
        // Invalidation since we use this also on removeView
        mCardBackLayout = null;
        mCardFrontLayout = null;

        int childs = getChildCount();
        if (childs < 1) {
            return;
        }

        if (childs < 2) {
            // Only invalidate flip state if we have a single child
            mFlipState = FlipState.FRONT_SIDE;

            mCardFrontLayout = getChildAt(0);
        } else if (childs == 2) {
            mCardFrontLayout = getChildAt(1);
            mCardBackLayout = getChildAt(0);
        }

        if (!isFlipOnTouch()) {
            mCardFrontLayout.setVisibility(VISIBLE);

            if (mCardBackLayout != null) {
                mCardBackLayout.setVisibility(GONE);
            }
        }
    }

    private void setupInitializations() {
        mCardBackLayout.setVisibility(View.GONE);
    }

    private void loadAnimations() {
        if (flipType.equalsIgnoreCase("horizontal")) {

            if (flipTypeFrom.equalsIgnoreCase("left")) {
                int animFlipHorizontalOutId = R.animator.animation_horizontal_flip_out;
                mSetRightOut =
                        (AnimatorSet) AnimatorInflater.loadAnimator(this.context, animFlipHorizontalOutId);
                int animFlipHorizontalInId = R.animator.animation_horizontal_flip_in;
                mSetLeftIn =
                        (AnimatorSet) AnimatorInflater.loadAnimator(this.context, animFlipHorizontalInId);
            } else {
                int animFlipHorizontalRightOutId = R.animator.animation_horizontal_right_out;
                mSetRightOut =
                        (AnimatorSet) AnimatorInflater.loadAnimator(this.context, animFlipHorizontalRightOutId);
                int animFlipHorizontalRightInId = R.animator.animation_horizontal_right_in;
                mSetLeftIn =
                        (AnimatorSet) AnimatorInflater.loadAnimator(this.context, animFlipHorizontalRightInId);
            }


            if (mSetRightOut == null || mSetLeftIn == null) {
                throw new RuntimeException(
                        "No Animations Found! Please set Flip in and Flip out animation Ids.");
            }

            mSetRightOut.removeAllListeners();
            mSetRightOut.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {

                }

                @Override
                public void onAnimationEnd(Animator animator) {

                    if (mFlipState == FlipState.FRONT_SIDE) {
                        mCardBackLayout.setVisibility(GONE);
                        mCardFrontLayout.setVisibility(VISIBLE);

                        if (onFlipListener != null)
                            onFlipListener.onViewFlipCompleted(FlipView.this, FlipState.FRONT_SIDE);
                    } else {
                        mCardBackLayout.setVisibility(VISIBLE);
                        mCardFrontLayout.setVisibility(GONE);

                        if (onFlipListener != null)
                            onFlipListener.onViewFlipCompleted(FlipView.this, FlipState.BACK_SIDE);

                        // Auto Flip Back
                        if (autoFlipBack) {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    flipTheView();
                                }
                            }, autoFlipBackTime);
                        }
                    }
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });
        } else {

            if (!TextUtils.isEmpty(flipTypeFrom) && flipTypeFrom.equalsIgnoreCase("front")) {
                int animFlipVerticalFrontOutId = R.animator.animation_vertical_front_out;
                mSetTopOut = (AnimatorSet) AnimatorInflater.loadAnimator(this.context, animFlipVerticalFrontOutId);
                int animFlipVerticalFrontInId = R.animator.animation_vertical_flip_front_in;
                mSetBottomIn =
                        (AnimatorSet) AnimatorInflater.loadAnimator(this.context, animFlipVerticalFrontInId);
            } else {
                int animFlipVerticalOutId = R.animator.animation_vertical_flip_out;
                mSetTopOut = (AnimatorSet) AnimatorInflater.loadAnimator(this.context, animFlipVerticalOutId);
                int animFlipVerticalInId = R.animator.animation_vertical_flip_in;
                mSetBottomIn =
                        (AnimatorSet) AnimatorInflater.loadAnimator(this.context, animFlipVerticalInId);
            }

            if (mSetTopOut == null || mSetBottomIn == null) {
                throw new RuntimeException(
                        "No Animations Found! Please set Flip in and Flip out animation Ids.");
            }

            mSetTopOut.removeAllListeners();
            mSetTopOut.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {

                }

                @Override
                public void onAnimationEnd(Animator animator) {

                    if (mFlipState == FlipState.FRONT_SIDE) {
                        mCardBackLayout.setVisibility(GONE);
                        mCardFrontLayout.setVisibility(VISIBLE);

                        if (onFlipListener != null)
                            onFlipListener.onViewFlipCompleted(FlipView.this, FlipState.FRONT_SIDE);
                    } else {
                        mCardBackLayout.setVisibility(VISIBLE);
                        mCardFrontLayout.setVisibility(GONE);

                        if (onFlipListener != null)
                            onFlipListener.onViewFlipCompleted(FlipView.this, FlipState.BACK_SIDE);

                        // Auto Flip Back
                        if (autoFlipBack) {
                            new Handler().postDelayed(() -> flipTheView(), autoFlipBackTime);
                        }
                    }
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });
        }
        setFlipDuration(flipDuration);
    }

    private void changeCameraDistance() {
        int distance = 8000;
        float scale = getResources().getDisplayMetrics().density * distance;

        if (mCardFrontLayout != null) {
            mCardFrontLayout.setCameraDistance(scale);
        }
        if (mCardBackLayout != null) {
            mCardBackLayout.setCameraDistance(scale);
        }
    }

    public void flipTheView() {
        if (!flipEnabled || getChildCount() < 2) return;

        if (flipOnceEnabled && mFlipState == FlipState.BACK_SIDE)
            return;

        boolean mIsBackVisible = false;
        if (flipType.equalsIgnoreCase("horizontal")) {
            if (mSetRightOut.isRunning() || mSetLeftIn.isRunning()) return;

            mCardBackLayout.setVisibility(VISIBLE);
            mCardFrontLayout.setVisibility(VISIBLE);

            if (mFlipState == FlipState.FRONT_SIDE) {
                // From front to back
                mSetRightOut.setTarget(mCardFrontLayout);
                mSetLeftIn.setTarget(mCardBackLayout);
                mSetRightOut.start();
                mSetLeftIn.start();
                mIsBackVisible = true;
                mFlipState = FlipState.BACK_SIDE;
            } else {
                // from back to front
                mSetRightOut.setTarget(mCardBackLayout);
                mSetLeftIn.setTarget(mCardFrontLayout);
                mSetRightOut.start();
                mSetLeftIn.start();
                mIsBackVisible = false;
                mFlipState = FlipState.FRONT_SIDE;
            }
        } else {
            if (mSetTopOut.isRunning() || mSetBottomIn.isRunning()) return;

            mCardBackLayout.setVisibility(VISIBLE);
            mCardFrontLayout.setVisibility(VISIBLE);

            if (mFlipState == FlipState.FRONT_SIDE) {
                // From front to back
                mSetTopOut.setTarget(mCardFrontLayout);
                mSetBottomIn.setTarget(mCardBackLayout);
                mSetTopOut.start();
                mSetBottomIn.start();
                mIsBackVisible = true;
                mFlipState = FlipState.BACK_SIDE;
            } else {
                // from back to front
                mSetTopOut.setTarget(mCardBackLayout);
                mSetBottomIn.setTarget(mCardFrontLayout);
                mSetTopOut.start();
                mSetBottomIn.start();
                mIsBackVisible = false;
                mFlipState = FlipState.FRONT_SIDE;
            }
        }
    }

    public void flipTheView(boolean withAnimation) {
        if (getChildCount() < 2) return;

        if (flipType.equalsIgnoreCase("horizontal")) {
            if (!withAnimation) {
                mSetLeftIn.setDuration(0);
                mSetRightOut.setDuration(0);
                boolean oldFlipEnabled = flipEnabled;
                flipEnabled = true;

                flipTheView();

                mSetLeftIn.setDuration(flipDuration);
                mSetRightOut.setDuration(flipDuration);
                flipEnabled = oldFlipEnabled;
            } else {
                flipTheView();
            }
        } else {
            if (!withAnimation) {
                mSetBottomIn.setDuration(0);
                mSetTopOut.setDuration(0);
                boolean oldFlipEnabled = flipEnabled;
                flipEnabled = true;

                flipTheView();

                mSetBottomIn.setDuration(flipDuration);
                mSetTopOut.setDuration(flipDuration);
                flipEnabled = oldFlipEnabled;
            } else {
                flipTheView();
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (isEnabled() && flipOnTouch) {
            this.getParent().requestDisallowInterceptTouchEvent(true);
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    x1 = event.getX();
                    y1 = event.getY();
                    return true;
                case MotionEvent.ACTION_UP:
                    float x2 = event.getX();
                    float y2 = event.getY();
                    float dx = x2 - x1;
                    float dy = y2 - y1;
                    float MAX_CLICK_DISTANCE = 0.5f;
                    if ((dx >= 0 && dx < MAX_CLICK_DISTANCE) && (dy >= 0 && dy < MAX_CLICK_DISTANCE)) {
                        flipTheView();
                    }
                    return true;
            }
        } else {
            return super.onTouchEvent(event);
        }
        return super.onTouchEvent(event);
    }

    public boolean isFlipOnTouch() {
        return flipOnTouch;
    }

    public void setFlipOnTouch(boolean flipOnTouch) {
        this.flipOnTouch = flipOnTouch;
    }

    public int getFlipDuration() {
        return flipDuration;
    }

    public void setFlipDuration(int flipDuration) {
        this.flipDuration = flipDuration;
        if (flipType.equalsIgnoreCase("horizontal")) {
            //mSetRightOut.setDuration(flipDuration);
            mSetRightOut.getChildAnimations().get(0).setDuration(flipDuration);
            mSetRightOut.getChildAnimations().get(1).setStartDelay(flipDuration / 2);

            //mSetLeftIn.setDuration(flipDuration);
            mSetLeftIn.getChildAnimations().get(1).setDuration(flipDuration);
            mSetLeftIn.getChildAnimations().get(2).setStartDelay(flipDuration / 2);
        } else {
            mSetTopOut.getChildAnimations().get(0).setDuration(flipDuration);
            mSetTopOut.getChildAnimations().get(1).setStartDelay(flipDuration / 2);

            mSetBottomIn.getChildAnimations().get(1).setDuration(flipDuration);
            mSetBottomIn.getChildAnimations().get(2).setStartDelay(flipDuration / 2);
        }
    }

    public boolean isFlipOnceEnabled() {
        return flipOnceEnabled;
    }

    public void setFlipOnceEnabled(boolean flipOnceEnabled) {
        this.flipOnceEnabled = flipOnceEnabled;
    }

    public boolean isFlipEnabled() {
        return flipEnabled;
    }

    public void setFlipEnabled(boolean flipEnabled) {
        this.flipEnabled = flipEnabled;
    }

    public FlipState getCurrentFlipState() {
        return mFlipState;
    }

    public boolean isFrontSide() {
        return (mFlipState == FlipState.FRONT_SIDE);
    }

    public boolean isBackSide() {
        return (mFlipState == FlipState.BACK_SIDE);
    }

    public OnFlipAnimationListener getOnFlipListener() {
        return onFlipListener;
    }

    public void setOnFlipListener(OnFlipAnimationListener onFlipListener) {
        this.onFlipListener = onFlipListener;
    }

    public boolean isHorizontalType() {
        return flipType.equals("horizontal");
    }

    public boolean isVerticalType() {
        return flipType.equals("vertical");
    }

    public void setToHorizontalType() {
        flipType = "horizontal";
        loadAnimations();
    }

    public void setToVerticalType() {
        flipType = "vertical";
        loadAnimations();
    }

    public void setFlipTypeFromRight() {
        if (flipType.equals("horizontal"))
            flipTypeFrom = "right";
        else flipTypeFrom = "front";
        loadAnimations();
    }

    public void setFlipTypeFromLeft() {
        if (flipType.equals("horizontal"))
            flipTypeFrom = "left";
        else flipTypeFrom = "back";
        loadAnimations();
    }

    public void setFlipTypeFromFront() {
        if (flipType.equals("vertical"))
            flipTypeFrom = "front";
        else flipTypeFrom = "right";
        loadAnimations();
    }

    public void setFlipTypeFromBack() {
        if (flipType.equals("vertical"))
            flipTypeFrom = "back";
        else flipTypeFrom = "left";
        loadAnimations();
    }

    public String getFlipTypeFrom() {
        return flipTypeFrom;
    }

    public boolean isAutoFlipBack() {
        return autoFlipBack;
    }

    public void setAutoFlipBack(boolean autoFlipBack) {
        this.autoFlipBack = autoFlipBack;
    }

    public int getAutoFlipBackTime() {
        return autoFlipBackTime;
    }

    public void setAutoFlipBackTime(int autoFlipBackTime) {
        this.autoFlipBackTime = autoFlipBackTime;
    }

    public interface OnFlipAnimationListener {
        void onViewFlipCompleted(FlipView easyFlipView, FlipState newCurrentSide);
    }
}
