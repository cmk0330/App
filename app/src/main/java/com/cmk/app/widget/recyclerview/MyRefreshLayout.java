package com.cmk.app.widget.recyclerview;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Transformation;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Px;
import androidx.annotation.VisibleForTesting;
import androidx.core.content.ContextCompat;
import androidx.core.view.NestedScrollingChild;
import androidx.core.view.NestedScrollingChild2;
import androidx.core.view.NestedScrollingChild3;
import androidx.core.view.NestedScrollingChildHelper;
import androidx.core.view.NestedScrollingParent;
import androidx.core.view.NestedScrollingParent2;
import androidx.core.view.NestedScrollingParent3;
import androidx.core.view.NestedScrollingParentHelper;
import androidx.core.view.ViewCompat;
import androidx.core.widget.ListViewCompat;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

public class MyRefreshLayout extends ViewGroup implements NestedScrollingParent3,
        NestedScrollingParent2, NestedScrollingChild3, NestedScrollingChild2, NestedScrollingParent,
        NestedScrollingChild {
    // Maps to ProgressBar.Large style
    public static final int LARGE = CircularProgressDrawable.LARGE;
    // Maps to ProgressBar default style
    public static final int DEFAULT = CircularProgressDrawable.DEFAULT;

    public static final int DEFAULT_SLINGSHOT_DISTANCE = -1;

    @VisibleForTesting
    static final int CIRCLE_DIAMETER = 40;
    @VisibleForTesting
    static final int CIRCLE_DIAMETER_LARGE = 56;

    private static final String LOG_TAG = MyRefreshLayout.class.getSimpleName();

    private static final int MAX_ALPHA = 255;
    private static final int STARTING_PROGRESS_ALPHA = (int) (.3f * MAX_ALPHA);

    private static final float DECELERATE_INTERPOLATION_FACTOR = 2f;
    private static final int INVALID_POINTER = -1;
    private static final float DRAG_RATE = .5f;

    // Max amount of circle that can be filled by progress during swipe gesture,
    // where 1.0 is a full circle
    private static final float MAX_PROGRESS_ANGLE = .8f;

    private static final int SCALE_DOWN_DURATION = 150;

    private static final int ALPHA_ANIMATION_DURATION = 300;

    private static final int ANIMATE_TO_TRIGGER_DURATION = 200;

    private static final int ANIMATE_TO_START_DURATION = 200;

    // Default offset in dips from the top of the view to where the progress spinner should stop
    private static final int DEFAULT_CIRCLE_TARGET = 64;

    private View mTarget; // the target of the gesture
    OnRefreshListener mListener;
    private RelativeLayout viewRefreshContainer;
    boolean mRefreshing = false;
    private int mTouchSlop;
    private float mTotalDragDistance = -1;

    // If nested scrolling is enabled, the total amount that needed to be
    // consumed by this as the nested scrolling parent is used in place of the
    // overscroll determined by MOVE events in the onTouch handler
    private float mTotalUnconsumed;
    private final NestedScrollingParentHelper mNestedScrollingParentHelper;
    private final NestedScrollingChildHelper mNestedScrollingChildHelper;
    private final int[] mParentScrollConsumed = new int[2];
    private final int[] mParentOffsetInWindow = new int[2];

    // Used for calls from old versions of onNestedScroll to v3 version of onNestedScroll. This only
    // exists to prevent GC costs that are present before API 21.
    private final int[] mNestedScrollingV2ConsumedCompat = new int[2];
    private boolean mNestedScrollInProgress;

    private int mMediumAnimationDuration;
    int mCurrentTargetOffsetTop;

    private float mInitialMotionY;
    private float mInitialDownY;
    private boolean mIsBeingDragged;
    private int mActivePointerId = INVALID_POINTER;
    // Whether this item is scaled up rather than clipped
    boolean mScale;

    // Target is returning to its start offset because it was cancelled or a
    // refresh was triggered.
    private boolean mReturningToStart;
    private final DecelerateInterpolator mDecelerateInterpolator;
    private static final int[] LAYOUT_ATTRS = new int[]{
            android.R.attr.enabled
    };

    private int mCircleViewIndex = -1;

    protected int mFrom;

    float mStartingScale;

    protected int mOriginalOffsetTop;

    int mSpinnerOffsetEnd;

    int mCustomSlingshotDistance;

    CircularProgressDrawable mProgress;

    private Animation mScaleAnimation;

    private Animation mScaleDownAnimation;

    private Animation mAlphaStartAnimation;

    private Animation mAlphaMaxAnimation;

    private Animation mScaleDownToStartAnimation;

    boolean mNotify;

    private int mCircleDiameter;

    // Whether the client has set a custom starting position;
    boolean mUsingCustomStart;

    private OnChildScrollUpCallback mChildScrollUpCallback;

    /**
     * @see #setLegacyRequestDisallowInterceptTouchEventEnabled
     */
    private boolean mEnableLegacyRequestDisallowInterceptTouch;

    private Animation.AnimationListener mRefreshListener = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            if (mRefreshing) {
                // Make sure the progress view is fully visible
                mProgress.setAlpha(MAX_ALPHA);
                mProgress.start();
                if (mNotify) {
                    if (mListener != null) {
                        mListener.onRefresh();
                    }
                }
                mCurrentTargetOffsetTop = viewRefreshContainer.getTop();
            } else {
                reset();
            }
        }
    };
    private DefaultRefreshView mCircleView;
    private int viewContentHeight;

    void reset() {
        viewRefreshContainer.clearAnimation();
        mProgress.stop();
        viewRefreshContainer.setVisibility(View.GONE);
        setColorViewAlpha(MAX_ALPHA);
        // Return the circle to its start position
        if (mScale) {
            setAnimationProgress(0 /* animation complete and view is hidden */);
        } else {
            setTargetOffsetTopAndBottom(mOriginalOffsetTop - mCurrentTargetOffsetTop);
        }
//        mCurrentTargetOffsetTop = mCircleView.getTop();
        mCurrentTargetOffsetTop = viewRefreshContainer.getTop();
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if (!enabled) {
            reset();
        }
    }

    static class SavedState extends BaseSavedState {
        final boolean mRefreshing;

        /**
         * Constructor called from {@link MyRefreshLayout#onSaveInstanceState()}
         */
        SavedState(Parcelable superState, boolean refreshing) {
            super(superState);
            this.mRefreshing = refreshing;
        }

        /**
         * Constructor called from {@link #CREATOR}
         */
        SavedState(Parcel in) {
            super(in);
            mRefreshing = in.readByte() != 0;
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeByte(mRefreshing ? (byte) 1 : (byte) 0);
        }

        public static final Creator<SavedState> CREATOR =
                new Creator<SavedState>() {
                    @Override
                    public SavedState createFromParcel(Parcel in) {
                        return new SavedState(in);
                    }

                    @Override
                    public SavedState[] newArray(int size) {
                        return new SavedState[size];
                    }
                };
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        return new SavedState(superState, mRefreshing);
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        SavedState savedState = (SavedState) state;
        super.onRestoreInstanceState(savedState.getSuperState());
        setRefreshing(savedState.mRefreshing);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        reset();
    }

    private void setColorViewAlpha(int targetAlpha) {
        mCircleView.getBackground().setAlpha(targetAlpha);
        mProgress.setAlpha(targetAlpha);
    }


    public void setProgressViewOffset(boolean scale, int start, int end) {
        mScale = scale;
        mOriginalOffsetTop = start;
        mSpinnerOffsetEnd = end;
        mUsingCustomStart = true;
        reset();
        mRefreshing = false;
    }

    /**
     * @return The offset in pixels from the top of this view at which the progress spinner should
     * appear.
     */
    public int getProgressViewStartOffset() {
        return mOriginalOffsetTop;
    }

    /**
     * @return The offset in pixels from the top of this view at which the progress spinner should
     * come to rest after a successful swipe gesture.
     */
    public int getProgressViewEndOffset() {
        return mSpinnerOffsetEnd;
    }


    public void setProgressViewEndTarget(boolean scale, int end) {
        mSpinnerOffsetEnd = end;
        mScale = scale;
        viewRefreshContainer.invalidate();
    }

    /**
     * Sets the distance that the refresh indicator can be pulled beyond its resting position during
     * a swipe gesture. The default is {@link #DEFAULT_SLINGSHOT_DISTANCE}.
     *
     * @param slingshotDistance The distance in pixels that the refresh indicator can be pulled
     *                          beyond its resting position.
     */
    public void setSlingshotDistance(@Px int slingshotDistance) {
        mCustomSlingshotDistance = slingshotDistance;
    }

    /**
     * One of DEFAULT, or LARGE.
     */
    public void setSize(int size) {
        if (size != CircularProgressDrawable.LARGE && size != CircularProgressDrawable.DEFAULT) {
            return;
        }
        final DisplayMetrics metrics = getResources().getDisplayMetrics();
        if (size == CircularProgressDrawable.LARGE) {
            mCircleDiameter = (int) (CIRCLE_DIAMETER_LARGE * metrics.density);
        } else {
            mCircleDiameter = (int) (CIRCLE_DIAMETER * metrics.density);
        }
        // force the bounds of the progress circle inside the circle view to
        // update by setting it to null before updating its size and then
        // re-setting it
//        mCircleView.setImageDrawable(null);
        mProgress.setStyle(size);
//        mCircleView.setImageDrawable(mProgress);
    }

    /**
     * Simple constructor to use when creating a SwipeRefreshLayout from code.
     *
     * @param context
     */
    public MyRefreshLayout(@NonNull Context context) {
        this(context, null);
    }

    /**
     * Constructor that is called when inflating SwipeRefreshLayout from XML.
     *
     * @param context
     * @param attrs
     */
    public MyRefreshLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();

        mMediumAnimationDuration = getResources().getInteger(
                android.R.integer.config_mediumAnimTime);

        setWillNotDraw(false);
        mDecelerateInterpolator = new DecelerateInterpolator(DECELERATE_INTERPOLATION_FACTOR);

        final DisplayMetrics metrics = getResources().getDisplayMetrics();
        mCircleDiameter = (int) (CIRCLE_DIAMETER * metrics.density);

        createProgressView();
        setChildrenDrawingOrderEnabled(true);
        // the absolute offset has to take into account that the circle starts at an offset
        mSpinnerOffsetEnd = (int) (DEFAULT_CIRCLE_TARGET * metrics.density);
        mTotalDragDistance = mSpinnerOffsetEnd;
        mNestedScrollingParentHelper = new NestedScrollingParentHelper(this);

        mNestedScrollingChildHelper = new NestedScrollingChildHelper(this);
        setNestedScrollingEnabled(true);

        mOriginalOffsetTop = mCurrentTargetOffsetTop = -mCircleDiameter;
        moveToStart(1.0f);

        final TypedArray a = context.obtainStyledAttributes(attrs, LAYOUT_ATTRS);
        setEnabled(a.getBoolean(0, true));
        a.recycle();
    }

    @Override
    protected int getChildDrawingOrder(int childCount, int i) {
        if (mCircleViewIndex < 0) {
            return i;
        } else if (i == childCount - 1) {
            // Draw the selected child last
            return mCircleViewIndex;
        } else if (i >= mCircleViewIndex) {
            // Move the children after the selected child earlier one
            return i + 1;
        } else {
            // Keep the children before the selected child the same
            return i;
        }
    }

    private void createProgressView() {
        viewRefreshContainer = new RelativeLayout(getContext());
        viewRefreshContainer.setGravity(Gravity.CENTER);
        mCircleView = new DefaultRefreshView(getContext());
        mProgress = new CircularProgressDrawable(getContext());
        mProgress.setStyle(CircularProgressDrawable.DEFAULT);
//        mCircleView.setImageDrawable(mProgress);
        mCircleView.setVisibility(View.GONE);
        viewRefreshContainer.addView(mCircleView);
        addView(viewRefreshContainer);
    }

    /**
     * Set the listener to be notified when a refresh is triggered via the swipe
     * gesture.
     */
    public void setOnRefreshListener(@Nullable OnRefreshListener listener) {
        mListener = listener;
    }

    /**
     * Notify the widget that refresh state has changed. Do not call this when
     * refresh is triggered by a swipe gesture.
     *
     * @param refreshing Whether or not the view should show refresh progress.
     */
    public void setRefreshing(boolean refreshing) {
        if (refreshing && mRefreshing != refreshing) {
            // scale and show
            mRefreshing = refreshing;
            int endTarget = 0;
            if (!mUsingCustomStart) {
                endTarget = mSpinnerOffsetEnd + mOriginalOffsetTop;
            } else {
                endTarget = mSpinnerOffsetEnd;
            }
            setTargetOffsetTopAndBottom(endTarget - mCurrentTargetOffsetTop);
            mNotify = false;
            startScaleUpAnimation(mRefreshListener);
        } else {
            setRefreshing(refreshing, false /* notify */);
        }
    }

    private void startScaleUpAnimation(Animation.AnimationListener listener) {
//        mCircleView.setVisibility(View.VISIBLE);
        mProgress.setAlpha(MAX_ALPHA);
        mScaleAnimation = new Animation() {
            @Override
            public void applyTransformation(float interpolatedTime, Transformation t) {
                setAnimationProgress(interpolatedTime);
            }
        };
        mScaleAnimation.setDuration(mMediumAnimationDuration);
        if (listener != null) {
//            mCircleView.setAnimationListener(listener);
        }
//        mCircleView.clearAnimation();
//        mCircleView.startAnimation(mScaleAnimation);
    }

    /**
     * Pre API 11, this does an alpha animation.
     *
     * @param progress
     */
    void setAnimationProgress(float progress) {
//        mCircleView.setScaleX(progress);
//        mCircleView.setScaleY(progress);
    }

    private void setRefreshing(boolean refreshing, final boolean notify) {
        if (mRefreshing != refreshing) {
            mNotify = notify;
            ensureTarget();
            mRefreshing = refreshing;
            if (mRefreshing) {
                animateOffsetToCorrectPosition(mCurrentTargetOffsetTop, mRefreshListener);
            } else {
                startScaleDownAnimation(mRefreshListener);
            }
        }
    }

    void startScaleDownAnimation(Animation.AnimationListener listener) {
        mScaleDownAnimation = new Animation() {
            @Override
            public void applyTransformation(float interpolatedTime, Transformation t) {
                setAnimationProgress(1 - interpolatedTime);
            }
        };
        mScaleDownAnimation.setDuration(SCALE_DOWN_DURATION);
//        mCircleView.setAnimationListener(listener);
//        mCircleView.clearAnimation();
//        mCircleView.startAnimation(mScaleDownAnimation);
    }

    private void startProgressAlphaStartAnimation() {
        mAlphaStartAnimation = startAlphaAnimation(mProgress.getAlpha(), STARTING_PROGRESS_ALPHA);
    }

    private void startProgressAlphaMaxAnimation() {
        mAlphaMaxAnimation = startAlphaAnimation(mProgress.getAlpha(), MAX_ALPHA);
    }

    private Animation startAlphaAnimation(final int startingAlpha, final int endingAlpha) {
        Animation alpha = new Animation() {
            @Override
            public void applyTransformation(float interpolatedTime, Transformation t) {
                mProgress.setAlpha(
                        (int) (startingAlpha + ((endingAlpha - startingAlpha) * interpolatedTime)));
            }
        };
        alpha.setDuration(ALPHA_ANIMATION_DURATION);
        // Clear out the previous animation listeners.
//        mCircleView.setAnimationListener(null);
//        mCircleView.clearAnimation();
//        mCircleView.startAnimation(alpha);
        return alpha;
    }

    /**
     * @deprecated Use {@link #setProgressBackgroundColorSchemeResource(int)}
     */
    @Deprecated
    public void setProgressBackgroundColor(int colorRes) {
        setProgressBackgroundColorSchemeResource(colorRes);
    }

    /**
     * Set the background color of the progress spinner disc.
     *
     * @param colorRes Resource id of the color.
     */
    public void setProgressBackgroundColorSchemeResource(@ColorRes int colorRes) {
        setProgressBackgroundColorSchemeColor(ContextCompat.getColor(getContext(), colorRes));
    }

    /**
     * Set the background color of the progress spinner disc.
     *
     * @param color
     */
    public void setProgressBackgroundColorSchemeColor(@ColorInt int color) {
//        mCircleView.setBackgroundColor(color);
    }

    /**
     * @deprecated Use {@link #setColorSchemeResources(int...)}
     */
    @Deprecated
    public void setColorScheme(@ColorRes int... colors) {
        setColorSchemeResources(colors);
    }

    /**
     * Set the color resources used in the progress animation from color resources.
     * The first color will also be the color of the bar that grows in response
     * to a user swipe gesture.
     *
     * @param colorResIds
     */
    public void setColorSchemeResources(@ColorRes int... colorResIds) {
        final Context context = getContext();
        int[] colorRes = new int[colorResIds.length];
        for (int i = 0; i < colorResIds.length; i++) {
            colorRes[i] = ContextCompat.getColor(context, colorResIds[i]);
        }
        setColorSchemeColors(colorRes);
    }

    /**
     * Set the colors used in the progress animation. The first
     * color will also be the color of the bar that grows in response to a user
     * swipe gesture.
     *
     * @param colors
     */
    public void setColorSchemeColors(@ColorInt int... colors) {
        ensureTarget();
        mProgress.setColorSchemeColors(colors);
    }

    /**
     * @return Whether the SwipeRefreshWidget is actively showing refresh
     * progress.
     */
    public boolean isRefreshing() {
        return mRefreshing;
    }

    private void ensureTarget() {
        // Don't bother getting the parent height if the parent hasn't been laid
        // out yet.
        if (mTarget == null) {
            for (int i = 0; i < getChildCount(); i++) {
                View child = getChildAt(i);
                if (!child.equals(viewRefreshContainer)) {
                    mTarget = child;
                    mTarget.setClickable(true);
                    break;
                }
            }
        }
    }

    /**
     * Set the distance to trigger a sync in dips
     *
     * @param distance
     */
    public void setDistanceToTriggerSync(int distance) {
        mTotalDragDistance = distance;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        final int width = getMeasuredWidth();
        final int height = getMeasuredHeight();
        viewContentHeight = height;
        if (getChildCount() == 0) {
            return;
        }
        if (mTarget == null) {
            ensureTarget();
        }
        if (mTarget == null) {
            return;
        }
        final View child = mTarget;
        final int childLeft = getPaddingLeft();
        final int childTop = getPaddingTop();
        final int childWidth = width - getPaddingLeft() - getPaddingRight();
        final int childHeight = height - getPaddingTop() - getPaddingBottom();
        child.layout(childLeft, childTop, childLeft + childWidth, childTop + childHeight);
        int circleWidth = viewRefreshContainer.getMeasuredWidth();
        int circleHeight = viewRefreshContainer.getMeasuredHeight();
        viewRefreshContainer.layout(0, -height / 2, width, height / 2);
//        viewRefreshContainer.layout((width / 2 - circleWidth / 2), mCurrentTargetOffsetTop,
//                (width / 2 + circleWidth / 2), mCurrentTargetOffsetTop + circleHeight);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mTarget == null) {
            ensureTarget();
        }
        if (mTarget == null) {
            return;
        }
        mTarget.measure(
                MeasureSpec.makeMeasureSpec(getMeasuredWidth() - getPaddingLeft() - getPaddingRight(), MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(getMeasuredHeight() - getPaddingTop() - getPaddingBottom(), MeasureSpec.EXACTLY));
        viewRefreshContainer.measure(
                MeasureSpec.makeMeasureSpec(getMeasuredWidth() - getPaddingLeft() - getPaddingRight(), MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(viewContentHeight, MeasureSpec.EXACTLY));
        mCircleViewIndex = -1;
        // Get the index of the circleview.
        for (int index = 0; index < getChildCount(); index++) {
            if (getChildAt(index) == viewRefreshContainer) {
                mCircleViewIndex = index;
                break;
            }
        }
    }

    /**
     * Get the diameter of the progress circle that is displayed as part of the
     * swipe to refresh layout.
     *
     * @return Diameter in pixels of the progress circle view.
     */
    public int getProgressCircleDiameter() {
        return mCircleDiameter;
    }

    /**
     * @return Whether it is possible for the child view of this layout to
     * scroll up. Override this if the child view is a custom view.
     */
    public boolean canChildScrollUp() {
        if (mChildScrollUpCallback != null) {
            return mChildScrollUpCallback.canChildScrollUp(this, mTarget);
        }
        if (mTarget instanceof ListView) {
            return ListViewCompat.canScrollList((ListView) mTarget, -1);
        }
        return mTarget.canScrollVertically(-1);
    }

    /**
     * Set a callback to override {@link #canChildScrollUp()} method. Non-null
     * callback will return the value provided by the callback and ignore all internal logic.
     *
     * @param callback Callback that should be called when canChildScrollUp() is called.
     */
    public void setOnChildScrollUpCallback(@Nullable OnChildScrollUpCallback callback) {
        mChildScrollUpCallback = callback;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        ensureTarget();

        final int action = ev.getActionMasked();
        int pointerIndex;

        if (mReturningToStart && action == MotionEvent.ACTION_DOWN) {
            mReturningToStart = false;
        }

        if (!isEnabled() || mReturningToStart || canChildScrollUp()
                || mRefreshing || mNestedScrollInProgress) {
            // Fail fast if we're not in a state where a swipe is possible
            return false;
        }

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                setTargetOffsetTopAndBottom(mOriginalOffsetTop - mCircleView.getTop());
                mActivePointerId = ev.getPointerId(0);
                mIsBeingDragged = false;

                pointerIndex = ev.findPointerIndex(mActivePointerId);
                if (pointerIndex < 0) {
                    return false;
                }
                mInitialDownY = ev.getY(pointerIndex);
                break;

            case MotionEvent.ACTION_MOVE:
                if (mActivePointerId == INVALID_POINTER) {
                    Log.e(LOG_TAG, "Got ACTION_MOVE event but don't have an active pointer id.");
                    return false;
                }

                pointerIndex = ev.findPointerIndex(mActivePointerId);
                if (pointerIndex < 0) {
                    return false;
                }
                final float y = ev.getY(pointerIndex);
                startDragging(y);
                break;

            case MotionEvent.ACTION_POINTER_UP:
                onSecondaryPointerUp(ev);
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mIsBeingDragged = false;
                mActivePointerId = INVALID_POINTER;
                break;
        }

        return mIsBeingDragged;
    }

    @Deprecated
    public void setLegacyRequestDisallowInterceptTouchEventEnabled(boolean enabled) {
        mEnableLegacyRequestDisallowInterceptTouch = enabled;
    }

    @Override
    public void requestDisallowInterceptTouchEvent(boolean b) {
        // if this is a List < L or another view that doesn't support nested
        // scrolling, ignore this request so that the vertical scroll event
        // isn't stolen
        if ((android.os.Build.VERSION.SDK_INT < 21 && mTarget instanceof AbsListView)
                || (mTarget != null && !ViewCompat.isNestedScrollingEnabled(mTarget))) {
            if (mEnableLegacyRequestDisallowInterceptTouch) {
                // Nope.
            } else {
                // Ignore here, but pass it up to our parent
                ViewParent parent = getParent();
                if (parent != null) {
                    parent.requestDisallowInterceptTouchEvent(b);
                }
            }
        } else {
            super.requestDisallowInterceptTouchEvent(b);
        }
    }

    // NestedScrollingParent 3

    @Override
    public void onNestedScroll(@NonNull View target, int dxConsumed, int dyConsumed,
                               int dxUnconsumed, int dyUnconsumed, @ViewCompat.NestedScrollType int type,
                               @NonNull int[] consumed) {
        if (type != ViewCompat.TYPE_TOUCH) {
            return;
        }

        // This is a bit of a hack. onNestedScroll is typically called up the hierarchy of nested
        // scrolling parents/children, where each consumes distances before passing the remainder
        // to parents.  In our case, we want to try to run after children, and after parents, so we
        // first pass scroll distances to parents and consume after everything else has.
        int consumedBeforeParents = consumed[1];
        dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed,
                mParentOffsetInWindow, type, consumed);
        int consumedByParents = consumed[1] - consumedBeforeParents;
        int unconsumedAfterParents = dyUnconsumed - consumedByParents;

        // There are two reasons why scroll distance may be totally consumed.  1) All of the nested
        // scrolling parents up the hierarchy implement NestedScrolling3 and consumed all of the
        // distance or 2) at least 1 nested scrolling parent doesn't implement NestedScrolling3 and
        // for comparability reasons, we are supposed to act like they have.
        //
        // We must assume 2) is the case because we have no way of determining that it isn't, and
        // therefore must fallback to a previous hack that was done before nested scrolling 3
        // existed.
        int remainingDistanceToScroll;
        if (unconsumedAfterParents == 0) {
            // The previously implemented hack is to see how far we were offset and assume that that
            // distance is equal to how much all of our parents consumed.
            remainingDistanceToScroll = dyUnconsumed + mParentOffsetInWindow[1];
        } else {
            remainingDistanceToScroll = unconsumedAfterParents;
        }

        // Not sure why we have to make sure the child can't scroll up... but seems dangerous to
        // remove.
        if (remainingDistanceToScroll < 0 && !canChildScrollUp()) {
            mTotalUnconsumed += Math.abs(remainingDistanceToScroll);
//            moveSpinner(mTotalUnconsumed);TODO

            // If we've gotten here, we need to consume whatever is left to consume, which at this
            // point is either equal to 0, or remainingDistanceToScroll.
            consumed[1] += unconsumedAfterParents;
        }
    }

    // NestedScrollingParent 2

    @Override
    public boolean onStartNestedScroll(View child, View target, int axes, int type) {
        if (type == ViewCompat.TYPE_TOUCH) {
            return onStartNestedScroll(child, target, axes);
        } else {
            return false;
        }
    }

    @Override
    public void onNestedScrollAccepted(View child, View target, int axes, int type) {
        // Should always be true because onStartNestedScroll returns false for all type !=
        // ViewCompat.TYPE_TOUCH, but check just in case.
        if (type == ViewCompat.TYPE_TOUCH) {
            onNestedScrollAccepted(child, target, axes);
        }
    }

    @Override
    public void onStopNestedScroll(View target, int type) {
        // Should always be true because onStartNestedScroll returns false for all type !=
        // ViewCompat.TYPE_TOUCH, but check just in case.
        if (type == ViewCompat.TYPE_TOUCH) {
            onStopNestedScroll(target);
        }
    }

    @Override
    public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed,
                               int dyUnconsumed, int type) {
        onNestedScroll(target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type,
                mNestedScrollingV2ConsumedCompat);
    }

    @Override
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed, int type) {
        // Should always be true because onStartNestedScroll returns false for all type !=
        // ViewCompat.TYPE_TOUCH, but check just in case.
        if (type == ViewCompat.TYPE_TOUCH) {
            onNestedPreScroll(target, dx, dy, consumed);
        }
    }

    // NestedScrollingParent 1

    @Override
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        return isEnabled() && !mReturningToStart && !mRefreshing
                && (nestedScrollAxes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0;
    }

    @Override
    public void onNestedScrollAccepted(View child, View target, int axes) {
        // Reset the counter of how much leftover scroll needs to be consumed.
        mNestedScrollingParentHelper.onNestedScrollAccepted(child, target, axes);
        // Dispatch up to the nested parent
        startNestedScroll(axes & ViewCompat.SCROLL_AXIS_VERTICAL);
        mTotalUnconsumed = 0;
        mNestedScrollInProgress = true;
    }

    @Override
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {
        // If we are in the middle of consuming, a scroll, then we want to move the spinner back up
        // before allowing the list to scroll
        if (dy > 0 && mTotalUnconsumed > 0) {
            if (dy > mTotalUnconsumed) {
                consumed[1] = (int) mTotalUnconsumed;
                mTotalUnconsumed = 0;
            } else {
                mTotalUnconsumed -= dy;
                consumed[1] = dy;
            }
//            moveSpinner(mTotalUnconsumed);TODO
        }

        // If a client layout is using a custom start position for the circle
        // view, they mean to hide it again before scrolling the child view
        // If we get back to mTotalUnconsumed == 0 and there is more to go, hide
        // the circle so it isn't exposed if its blocking content is moved
        if (mUsingCustomStart && dy > 0 && mTotalUnconsumed == 0
                && Math.abs(dy - consumed[1]) > 0) {
            viewRefreshContainer.setVisibility(View.GONE);
        }

        // Now let our nested parent consume the leftovers
        final int[] parentConsumed = mParentScrollConsumed;
        if (dispatchNestedPreScroll(dx - consumed[0], dy - consumed[1], parentConsumed, null)) {
            consumed[0] += parentConsumed[0];
            consumed[1] += parentConsumed[1];
        }
    }

    @Override
    public int getNestedScrollAxes() {
        return mNestedScrollingParentHelper.getNestedScrollAxes();
    }

    @Override
    public void onStopNestedScroll(View target) {
        mNestedScrollingParentHelper.onStopNestedScroll(target);
        mNestedScrollInProgress = false;
        // Finish the spinner for nested scrolling if we ever consumed any
        // unconsumed nested scroll
        if (mTotalUnconsumed > 0) {
//            finishSpinner(mTotalUnconsumed);TODO
            mTotalUnconsumed = 0;
        }
        // Dispatch up our nested parent
        stopNestedScroll();
    }

    @Override
    public void onNestedScroll(final View target, final int dxConsumed, final int dyConsumed,
                               final int dxUnconsumed, final int dyUnconsumed) {
        onNestedScroll(target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed,
                ViewCompat.TYPE_TOUCH, mNestedScrollingV2ConsumedCompat);
    }

    @Override
    public boolean onNestedPreFling(View target, float velocityX,
                                    float velocityY) {
        return dispatchNestedPreFling(velocityX, velocityY);
    }

    @Override
    public boolean onNestedFling(View target, float velocityX, float velocityY,
                                 boolean consumed) {
        return dispatchNestedFling(velocityX, velocityY, consumed);
    }

    // NestedScrollingChild 3

    @Override
    public void dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed,
                                     int dyUnconsumed, @Nullable int[] offsetInWindow, @ViewCompat.NestedScrollType int type,
                                     @NonNull int[] consumed) {
        if (type == ViewCompat.TYPE_TOUCH) {
            mNestedScrollingChildHelper.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed,
                    dyUnconsumed, offsetInWindow, type, consumed);
        }
    }

    // NestedScrollingChild 2

    @Override
    public boolean startNestedScroll(int axes, int type) {
        return type == ViewCompat.TYPE_TOUCH && startNestedScroll(axes);
    }

    @Override
    public void stopNestedScroll(int type) {
        if (type == ViewCompat.TYPE_TOUCH) {
            stopNestedScroll();
        }
    }

    @Override
    public boolean hasNestedScrollingParent(int type) {
        return type == ViewCompat.TYPE_TOUCH && hasNestedScrollingParent();
    }

    @Override
    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed,
                                        int dyUnconsumed, int[] offsetInWindow, int type) {
        return type == ViewCompat.TYPE_TOUCH && mNestedScrollingChildHelper.dispatchNestedScroll(
                dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow, type);
    }

    @Override
    public boolean dispatchNestedPreScroll(int dx, int dy, int[] consumed, int[] offsetInWindow,
                                           int type) {
        return type == ViewCompat.TYPE_TOUCH && dispatchNestedPreScroll(dx, dy, consumed,
                offsetInWindow);
    }

    // NestedScrollingChild 1

    @Override
    public void setNestedScrollingEnabled(boolean enabled) {
        mNestedScrollingChildHelper.setNestedScrollingEnabled(enabled);
    }

    @Override
    public boolean isNestedScrollingEnabled() {
        return mNestedScrollingChildHelper.isNestedScrollingEnabled();
    }

    @Override
    public boolean startNestedScroll(int axes) {
        return mNestedScrollingChildHelper.startNestedScroll(axes);
    }

    @Override
    public void stopNestedScroll() {
        mNestedScrollingChildHelper.stopNestedScroll();
    }

    @Override
    public boolean hasNestedScrollingParent() {
        return mNestedScrollingChildHelper.hasNestedScrollingParent();
    }

    @Override
    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed,
                                        int dyUnconsumed, int[] offsetInWindow) {
        return mNestedScrollingChildHelper.dispatchNestedScroll(dxConsumed, dyConsumed,
                dxUnconsumed, dyUnconsumed, offsetInWindow);
    }

    @Override
    public boolean dispatchNestedPreScroll(int dx, int dy, int[] consumed, int[] offsetInWindow) {
        return mNestedScrollingChildHelper.dispatchNestedPreScroll(
                dx, dy, consumed, offsetInWindow);
    }

    @Override
    public boolean dispatchNestedFling(float velocityX, float velocityY, boolean consumed) {
        return mNestedScrollingChildHelper.dispatchNestedFling(velocityX, velocityY, consumed);
    }

    @Override
    public boolean dispatchNestedPreFling(float velocityX, float velocityY) {
        return mNestedScrollingChildHelper.dispatchNestedPreFling(velocityX, velocityY);
    }

//    private boolean isAnimationRunning(Animation animation) {
//        return animation != null && animation.hasStarted() && !animation.hasEnded();
//    }

//    private void moveSpinner(float overscrollTop) {
//        mProgress.setArrowEnabled(true);
//        float originalDragPercent = overscrollTop / mTotalDragDistance;
//
//        float dragPercent = Math.min(1f, Math.abs(originalDragPercent));
//        float adjustedPercent = (float) Math.max(dragPercent - .4, 0) * 5 / 3;
//        float extraOS = Math.abs(overscrollTop) - mTotalDragDistance;
//        float slingshotDist = mCustomSlingshotDistance > 0
//                ? mCustomSlingshotDistance
//                : (mUsingCustomStart
//                ? mSpinnerOffsetEnd - mOriginalOffsetTop
//                : mSpinnerOffsetEnd);
//        float tensionSlingshotPercent = Math.max(0, Math.min(extraOS, slingshotDist * 2)
//                / slingshotDist);
//        float tensionPercent = (float) ((tensionSlingshotPercent / 4) - Math.pow(
//                (tensionSlingshotPercent / 4), 2)) * 2f;
//        float extraMove = slingshotDist * tensionPercent * 2;
//
//        int targetY = mOriginalOffsetTop + (int) ((slingshotDist * dragPercent) + extraMove);
//        // where 1.0f is a full circle
//        if (viewRefreshContainer.getVisibility() != View.VISIBLE) {
//            viewRefreshContainer.setVisibility(View.VISIBLE);
//        }
//        if (!mScale) {
//            viewRefreshContainer.setScaleX(1f);
//            viewRefreshContainer.setScaleY(1f);
//        }
//
//        if (mScale) {
//            setAnimationProgress(Math.min(1f, overscrollTop / mTotalDragDistance));
//        }
//        if (overscrollTop < mTotalDragDistance) {
//            if (mProgress.getAlpha() > STARTING_PROGRESS_ALPHA
//                    && !isAnimationRunning(mAlphaStartAnimation)) {
//                // Animate the alpha
//                startProgressAlphaStartAnimation();
//            }
//        } else {
//            if (mProgress.getAlpha() < MAX_ALPHA && !isAnimationRunning(mAlphaMaxAnimation)) {
//                // Animate the alpha
//                startProgressAlphaMaxAnimation();
//            }
//        }
//        float strokeStart = adjustedPercent * .8f;
//        mProgress.setStartEndTrim(0f, Math.min(MAX_PROGRESS_ANGLE, strokeStart));
//        mProgress.setArrowScale(Math.min(1f, adjustedPercent));
//
//        float rotation = (-0.25f + .4f * adjustedPercent + tensionPercent * 2) * .5f;
//        mProgress.setProgressRotation(rotation);
//        setTargetOffsetTopAndBottom(targetY - mCurrentTargetOffsetTop);
//    }

//    private void finishSpinner(float overscrollTop) {
//        if (overscrollTop > mTotalDragDistance) {
//            setRefreshing(true, true /* notify */);
//        } else {
//            // cancel refresh
//            mRefreshing = false;
//            mProgress.setStartEndTrim(0f, 0f);
//            Animation.AnimationListener listener = null;
//            if (!mScale) {
//                listener = new Animation.AnimationListener() {
//
//                    @Override
//                    public void onAnimationStart(Animation animation) {
//                    }
//
//                    @Override
//                    public void onAnimationEnd(Animation animation) {
//                        if (!mScale) {
//                            startScaleDownAnimation(null);
//                        }
//                    }
//
//                    @Override
//                    public void onAnimationRepeat(Animation animation) {
//                    }
//
//                };
//            }
//            animateOffsetToStartPosition(mCurrentTargetOffsetTop, listener);
//            mProgress.setArrowEnabled(false);
//        }
//    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        final int action = ev.getActionMasked();
        int pointerIndex = -1;

        if (mReturningToStart && action == MotionEvent.ACTION_DOWN) {
            mReturningToStart = false;
        }

        if (!isEnabled() || mReturningToStart || canChildScrollUp()
                || mRefreshing || mNestedScrollInProgress) {
            // Fail fast if we're not in a state where a swipe is possible
            return false;
        }

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mActivePointerId = ev.getPointerId(0);
                mIsBeingDragged = false;
                break;

            case MotionEvent.ACTION_MOVE: {
                pointerIndex = ev.findPointerIndex(mActivePointerId);
                if (pointerIndex < 0) {
                    Log.e(LOG_TAG, "Got ACTION_MOVE event but have an invalid active pointer id.");
                    return false;
                }

                final float y = ev.getY(pointerIndex);
                startDragging(y);

                if (mIsBeingDragged) {
                    final float overscrollTop = (y - mInitialMotionY) * DRAG_RATE;
                    if (overscrollTop > 0) {
                        // While the spinner is being dragged down, our parent shouldn't try
                        // to intercept touch events. It will stop the drag gesture abruptly.
                        getParent().requestDisallowInterceptTouchEvent(true);
//                        moveSpinner(overscrollTop);TODO
                    } else {
                        return false;
                    }
                }
                break;
            }
            case MotionEvent.ACTION_POINTER_DOWN: {
                pointerIndex = ev.getActionIndex();
                if (pointerIndex < 0) {
                    Log.e(LOG_TAG,
                            "Got ACTION_POINTER_DOWN event but have an invalid action index.");
                    return false;
                }
                mActivePointerId = ev.getPointerId(pointerIndex);
                break;
            }

            case MotionEvent.ACTION_POINTER_UP:
                onSecondaryPointerUp(ev);
                break;

            case MotionEvent.ACTION_UP: {
                pointerIndex = ev.findPointerIndex(mActivePointerId);
                if (pointerIndex < 0) {
                    Log.e(LOG_TAG, "Got ACTION_UP event but don't have an active pointer id.");
                    return false;
                }

                if (mIsBeingDragged) {
                    final float y = ev.getY(pointerIndex);
                    final float overscrollTop = (y - mInitialMotionY) * DRAG_RATE;
                    mIsBeingDragged = false;
//                    finishSpinner(overscrollTop);TODO
                }
                mActivePointerId = INVALID_POINTER;
                return false;
            }
            case MotionEvent.ACTION_CANCEL:
                return false;
        }

        return true;
    }

    private void startDragging(float y) {
        final float yDiff = y - mInitialDownY;
        if (yDiff > mTouchSlop && !mIsBeingDragged) {
            mInitialMotionY = mInitialDownY + mTouchSlop;
            mIsBeingDragged = true;
            mProgress.setAlpha(STARTING_PROGRESS_ALPHA);
        }
    }

    private void animateOffsetToCorrectPosition(int from, Animation.AnimationListener listener) {
        mFrom = from;
        mAnimateToCorrectPosition.reset();
        mAnimateToCorrectPosition.setDuration(ANIMATE_TO_TRIGGER_DURATION);
        mAnimateToCorrectPosition.setInterpolator(mDecelerateInterpolator);
        if (listener != null) {
//            mCircleView.setAnimationListener(listener);
        }
        mCircleView.clearAnimation();
        mCircleView.startAnimation(mAnimateToCorrectPosition);
    }

    private void animateOffsetToStartPosition(int from, Animation.AnimationListener listener) {
        if (mScale) {
            // Scale the item back down
            startScaleDownReturnToStartAnimation(from, listener);
        } else {
            mFrom = from;
            mAnimateToStartPosition.reset();
            mAnimateToStartPosition.setDuration(ANIMATE_TO_START_DURATION);
            mAnimateToStartPosition.setInterpolator(mDecelerateInterpolator);
            if (listener != null) {
//                mCircleView.setAnimationListener(listener);
            }
            viewRefreshContainer.clearAnimation();
            viewRefreshContainer.startAnimation(mAnimateToStartPosition);
        }
    }

    private final Animation mAnimateToCorrectPosition = new Animation() {
        @Override
        public void applyTransformation(float interpolatedTime, Transformation t) {
            int endTarget;
            if (!mUsingCustomStart) {
                endTarget = mSpinnerOffsetEnd - Math.abs(mOriginalOffsetTop);
            } else {
                endTarget = mSpinnerOffsetEnd;
            }
            int targetTop = (mFrom + (int) ((endTarget - mFrom) * interpolatedTime));
            int offset = targetTop - viewRefreshContainer.getTop();
            setTargetOffsetTopAndBottom(offset);
            mProgress.setArrowScale(1 - interpolatedTime);
        }
    };

    void moveToStart(float interpolatedTime) {
        int targetTop = (mFrom + (int) ((mOriginalOffsetTop - mFrom) * interpolatedTime));
        int offset = targetTop - viewRefreshContainer.getTop();
        setTargetOffsetTopAndBottom(offset);
    }

    private final Animation mAnimateToStartPosition = new Animation() {
        @Override
        public void applyTransformation(float interpolatedTime, Transformation t) {
            moveToStart(interpolatedTime);
        }
    };

    private void startScaleDownReturnToStartAnimation(int from,
                                                      Animation.AnimationListener listener) {
        mFrom = from;
        mStartingScale = viewRefreshContainer.getScaleX();
        mScaleDownToStartAnimation = new Animation() {
            @Override
            public void applyTransformation(float interpolatedTime, Transformation t) {
                float targetScale = (mStartingScale + (-mStartingScale * interpolatedTime));
                setAnimationProgress(targetScale);
                moveToStart(interpolatedTime);
            }
        };
        mScaleDownToStartAnimation.setDuration(SCALE_DOWN_DURATION);
        if (listener != null) {
//            mCircleView.setAnimationListener(listener);
        }
        viewRefreshContainer.clearAnimation();
        viewRefreshContainer.startAnimation(mScaleDownToStartAnimation);
    }

    void setTargetOffsetTopAndBottom(int offset) {
        viewRefreshContainer.bringToFront();
        ViewCompat.offsetTopAndBottom(viewRefreshContainer, offset);
        mCurrentTargetOffsetTop = viewRefreshContainer.getTop();
    }

    private void onSecondaryPointerUp(MotionEvent ev) {
        final int pointerIndex = ev.getActionIndex();
        final int pointerId = ev.getPointerId(pointerIndex);
        if (pointerId == mActivePointerId) {
            // This was our active pointer going up. Choose a new
            // active pointer and adjust accordingly.
            final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
            mActivePointerId = ev.getPointerId(newPointerIndex);
        }
    }

    /**
     * Classes that wish to be notified when the swipe gesture correctly
     * triggers a refresh should implement this interface.
     */
    public interface OnRefreshListener {
        /**
         * Called when a swipe gesture triggers a refresh.
         */
        void onRefresh();
    }

    /**
     * Classes that wish to override {@link #canChildScrollUp()} method
     * behavior should implement this interface.
     */
    public interface OnChildScrollUpCallback {
        /**
         * Callback that will be called when {@link #canChildScrollUp()} method
         * is called to allow the implementer to override its behavior.
         *
         * @param parent SwipeRefreshLayout that this callback is overriding.
         * @param child  The child view of SwipeRefreshLayout.
         * @return Whether it is possible for the child view of parent layout to scroll up.
         */
        boolean canChildScrollUp(@NonNull MyRefreshLayout parent, @Nullable View child);
    }
}
