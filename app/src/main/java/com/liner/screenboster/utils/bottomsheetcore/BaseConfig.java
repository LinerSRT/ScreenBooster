package com.liner.screenboster.utils.bottomsheetcore;

import android.view.animation.Interpolator;

import androidx.annotation.ColorInt;
import androidx.annotation.FloatRange;
import androidx.annotation.NonNull;

public interface BaseConfig {
    float MIN_DIM_AMOUNT = 0f;
    float MAX_DIM_AMOUNT = 1f;
    float DEFAULT_DIM_AMOUNT = 0.65f;
    long DEFAULT_ANIMATION_DURATION = 300L;

    @ColorInt
    int getDimColor();

    @FloatRange(from = 0.0f, to = 1.0f)
    float getDimAmount();

    float getTopGapSize();

    float getExtraPaddingTop();

    float getExtraPaddingBottom();

    float getMaxSheetWidth();

    @ColorInt
    int getSheetBackgroundColor();

    float getSheetCornerRadius();

    long getSheetAnimationDuration();

    @NonNull
    Interpolator getSheetAnimationInterpolator();

    boolean isDismissableOnTouchOutside();
}