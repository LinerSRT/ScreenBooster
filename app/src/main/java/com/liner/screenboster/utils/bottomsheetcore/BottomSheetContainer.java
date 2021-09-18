package com.liner.screenboster.utils.bottomsheetcore;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.liner.screenboster.utils.ViewUtils;


abstract class BottomSheetContainer extends FrameLayout implements BottomSheet {
    protected final BaseConfig mConfig;
    private float mSheetCornerRadius;
    private float mMaxSheetWidth;
    private float mStatusBarSize;
    private float mTopGapSize;

    private Point mDisplaySize;


    private int mDimColor;
    private int mSheetBackgroundColor;


    private long mAnimationDuration;
    private Interpolator mAnimationInterpolator;
    private ValueAnimator mAnimator;


    private ViewGroup mContentContainer;
    private FrameLayout mBottomSheetView;


    private State mState;

    private boolean mIsDismissableOnTouchOutside;


    private Runnable mViewManagementAction;
    private OnDismissListener mOnDismissListener;


    BottomSheetContainer(@NonNull Activity hostActivity, @NonNull BaseConfig config) {
        super(hostActivity);

        mConfig = config;

        init(hostActivity);
    }


    private void init(Activity hostActivity) {
        initContainer(hostActivity);
        initResources(hostActivity);
        initBottomSheet();
        requestWindowInsetsWhenAttached();
    }


    @SuppressWarnings("NewApi")
    private void initContainer(Activity hostActivity) {
        mContentContainer = hostActivity.findViewById(android.R.id.content);
        setElevation(999f);
    }


    private void initResources(Activity hostActivity) {
        initDimensions(hostActivity);
        initColors();
        initAnimations();
        initStates();
    }


    private void initDimensions(Activity hostActivity) {
        mSheetCornerRadius = mConfig.getSheetCornerRadius();
        mStatusBarSize = ViewUtils.getStatusBarSize(getContext());
        mTopGapSize = mConfig.getTopGapSize();
        mMaxSheetWidth = mConfig.getMaxSheetWidth();

        mDisplaySize = new Point();
        hostActivity.getWindowManager().getDefaultDisplay().getSize(mDisplaySize);
    }


    private void initColors() {
        mDimColor = mConfig.getDimColor();
        mSheetBackgroundColor = mConfig.getSheetBackgroundColor();
    }


    private void initAnimations() {
        mAnimationDuration = mConfig.getSheetAnimationDuration();
        mAnimationInterpolator = mConfig.getSheetAnimationInterpolator();
    }


    private void initStates() {
        mState = State.COLLAPSED;
        mIsDismissableOnTouchOutside = mConfig.isDismissableOnTouchOutside();
    }


    private void initBottomSheet() {
        mBottomSheetView = new FrameLayout(getContext());
        mBottomSheetView.setLayoutParams(generateDefaultLayoutParams());
        mBottomSheetView.setBackground(createBottomSheetBackgroundDrawable());
        mBottomSheetView.setPadding(
                mBottomSheetView.getPaddingLeft(),
                ((int) mConfig.getExtraPaddingTop()),
                mBottomSheetView.getPaddingRight(),
                ((int) mConfig.getExtraPaddingBottom())
        );
        final View createdSheetView = onCreateSheetContentView(getContext());
        mBottomSheetView.addView(createdSheetView);
        addView(mBottomSheetView);
        setBackgroundColor(mDimColor);
    }


    @Override
    public final WindowInsets onApplyWindowInsets(WindowInsets insets) {
        mBottomSheetView.setPadding(
                mBottomSheetView.getPaddingLeft(),
                mBottomSheetView.getPaddingTop(),
                mBottomSheetView.getPaddingRight(),
                (int) (insets.getSystemWindowInsetBottom() + mConfig.getExtraPaddingBottom())
        );

        return insets;
    }


    private void requestWindowInsetsWhenAttached() {
        if (isAttachedToWindow()) {
            requestApplyInsets();
        } else {
            addOnAttachStateChangeListener(new OnAttachStateChangeListener() {

                @Override
                public void onViewAttachedToWindow(View view) {
                    removeOnAttachStateChangeListener(this);
                    requestApplyInsets();
                }

                @Override
                public void onViewDetachedFromWindow(View view) {

                }

            });
        }
    }


    @Override
    protected final LayoutParams generateDefaultLayoutParams() {
        final LayoutParams layoutParams = new LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT
        );
        layoutParams.gravity = Gravity.CENTER_HORIZONTAL;

        return layoutParams;
    }


    private void addToContainer() {
        mContentContainer.addView(
                this,
                new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                )
        );
    }


    private void removeFromContainer() {
        mContentContainer.removeView(this);
    }


    @Override
    protected final void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        cancelStateTransitionAnimation();
    }


    @NonNull
    protected abstract View onCreateSheetContentView(@NonNull Context context);


    @Override
    public final boolean onTouchEvent(MotionEvent event) {
        if (mIsDismissableOnTouchOutside) {
            dismiss();
        }

        return true;
    }


    @Override
    protected final void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        setUiState(mState);
    }


    @Override
    protected final void measureChildWithMargins(View child,
                                                 int parentWidthMeasureSpec,
                                                 int widthUsed,
                                                 int parentHeightMeasureSpec,
                                                 int heightUsed) {
        final int parentWidth = MeasureSpec.getSize(parentWidthMeasureSpec);
        final int parentHeight = MeasureSpec.getSize(parentHeightMeasureSpec);
        final int displayHeight = mDisplaySize.y;
        final int verticalGapSize = (int) ((displayHeight > parentHeight) ? 0 : mStatusBarSize);
        final int maxWidth = (int) Math.min(parentWidth, mMaxSheetWidth);
        final int maxHeight = (int) (parentHeight - verticalGapSize - mTopGapSize);
        int adjustedParentWidthMeasureSpec = parentWidthMeasureSpec;
        int adjustedParentHeightMeasureSpec = parentHeightMeasureSpec;


        if (child == mBottomSheetView) {
            adjustedParentWidthMeasureSpec = MeasureSpec.makeMeasureSpec(maxWidth, MeasureSpec.getMode(parentWidthMeasureSpec));
            adjustedParentHeightMeasureSpec = MeasureSpec.makeMeasureSpec(maxHeight, MeasureSpec.getMode(parentHeightMeasureSpec));
        }

        super.measureChildWithMargins(
                child,
                adjustedParentWidthMeasureSpec,
                widthUsed,
                adjustedParentHeightMeasureSpec,
                heightUsed
        );
    }


    @Override
    public final void show() {
        show(true);
    }


    @Override
    public final void show(final boolean animate) {
        if (isAttachedToContainer()) {
            return;
        }
        cancelStateTransitionAnimation();
        addToContainer();
        postViewShowingAction(animate);
    }


    @Override
    public final void dismiss() {
        dismiss(true);
    }


    @Override
    public final void dismiss(boolean animate) {
        if (isAttachedToContainer() && (!animate || !State.COLLAPSING.equals(mState))) {
            cancelStateTransitionAnimation();
            reportOnDismiss();
            postViewDismissingAction(animate);
        }
    }


    private void postViewShowingAction(final boolean animate) {
        postPendingViewManagementAction(() -> BottomSheetContainer.this.expandSheet(animate));
    }


    private void expandSheet(final boolean animate) {
        if (animate) {
            if (!State.EXPANDED.equals(mState) && !State.EXPANDING.equals(mState)) {
                setUiState(State.COLLAPSED);
                animateStateTransition(State.EXPANDING);
            }
        } else {
            setUiState(mState = State.EXPANDED);
        }
    }


    private void postViewDismissingAction(final boolean animate) {
        postPendingViewManagementAction(() -> BottomSheetContainer.this.collapseSheet(animate));
    }


    private void collapseSheet(final boolean animate) {
        if (animate) {
            if (!State.COLLAPSED.equals(mState) && !State.COLLAPSING.equals(mState)) {
                animateStateTransition(State.COLLAPSING);
            }
        } else {
            removeFromContainer();
            setUiState(mState = State.COLLAPSED);
        }
    }


    private void postPendingViewManagementAction(Runnable action) {
        cancelPendingViewManagementAction();
        post(mViewManagementAction = action);
    }


    private void cancelPendingViewManagementAction() {
        if (mViewManagementAction != null) {
            removeCallbacks(mViewManagementAction);
        }
    }


    private boolean isAttachedToContainer() {
        final int containerChildCount = mContentContainer.getChildCount();

        for (int i = 0; i < containerChildCount; i++) {
            if (mContentContainer.getChildAt(i) == this) {
                return true;
            }
        }

        return false;
    }


    private void animateStateTransition(final State state) {

        cancelStateTransitionAnimation();


        final boolean isExpanding = State.EXPANDING.equals(state);
        final float startY = getMeasuredHeight();
        final float endY = (getMeasuredHeight() - mBottomSheetView.getMeasuredHeight());
        final float deltaY = (startY - endY);
        final float startValue = ((getMeasuredHeight() - mBottomSheetView.getY()) / deltaY);
        final float endValue = (isExpanding ? 1f : 0f);


        mAnimator = ValueAnimator.ofFloat(startValue, endValue);
        mAnimator.addUpdateListener(valueAnimator -> {
            final float animatedValue = (Float) valueAnimator.getAnimatedValue();
            final float newY = (startY + (animatedValue * (endY - startY)));
            mBottomSheetView.setY(newY);
            setAlpha(animatedValue);
        });
        mAnimator.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationStarted(Animator animation) {
                mState = (isExpanding ? State.EXPANDING : State.COLLAPSING);
            }

            @Override
            public void onAnimationEnded(Animator animation) {
                if (isExpanding) {
                    mState = State.EXPANDED;
                } else {
                    mState = State.COLLAPSED;

                    removeFromContainer();
                }
            }

        });
        mAnimator.setInterpolator(mAnimationInterpolator);
        mAnimator.setDuration(mAnimationDuration);
        mAnimator.start();
    }


    private void cancelStateTransitionAnimation() {
        if ((mAnimator != null) && mAnimator.isRunning()) {
            mAnimator.cancel();
        }
    }


    private void reportOnDismiss() {
        if (mOnDismissListener != null) {
            mOnDismissListener.onDismiss(this);
        }
    }


    private Drawable createBottomSheetBackgroundDrawable() {
        final GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(mSheetBackgroundColor);
        drawable.setCornerRadii(new float[]{
                mSheetCornerRadius,
                mSheetCornerRadius,
                mSheetCornerRadius,
                mSheetCornerRadius,
                0f,
                0f,
                0f,
                0f
        });

        return drawable;
    }


    private void setUiState(State state) {
        setBottomSheetState(state);
        setBackgroundState(state);
    }


    private void setBottomSheetState(State state) {
        if (State.EXPANDED.equals(state)) {
            mBottomSheetView.setY(getMeasuredHeight() - mBottomSheetView.getMeasuredHeight());
        } else if (State.COLLAPSED.equals(state)) {
            mBottomSheetView.setY(getMeasuredHeight());
        }
    }


    private void setBackgroundState(State state) {
        if (State.EXPANDED.equals(state)) {
            setAlpha(1f);
        } else if (State.COLLAPSED.equals(state)) {
            setAlpha(0f);
        }
    }


    @NonNull
    @Override
    public final State getState() {
        return mState;
    }


    @Override
    public final void setOnDismissListener(@Nullable OnDismissListener onDismissListener) {
        mOnDismissListener = onDismissListener;
    }


}