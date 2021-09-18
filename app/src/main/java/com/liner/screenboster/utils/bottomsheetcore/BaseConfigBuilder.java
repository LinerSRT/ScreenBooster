package com.liner.screenboster.utils.bottomsheetcore;

import android.view.animation.Interpolator;

import androidx.annotation.ColorInt;
import androidx.annotation.FloatRange;
import androidx.annotation.NonNull;


public interface BaseConfigBuilder<BT extends BaseConfigBuilder, CT> extends Builder<CT> {


    @NonNull
    BT dimAmount(@FloatRange(from = 0.0f, to = 1.0f) float dimAmount);


    @NonNull
    BT sheetCornerRadius(float cornerRadius);


    @NonNull
    BT topGapSize(float topGapSize);


    @NonNull
    BT maxSheetWidth(float maxWidth);


    @NonNull
    BT extraPaddingTop(float extraPaddingTop);


    @NonNull
    BT extraPaddingBottom(float extraPaddingBottom);


    @NonNull
    BT dimColor(@ColorInt int dimColor);


    @NonNull
    BT sheetBackgroundColor(@ColorInt int color);


    @NonNull
    BT sheetAnimationDuration(long animationDuration);


    @NonNull
    BT sheetAnimationInterpolator(@NonNull Interpolator interpolator);


    @NonNull
    BT dismissOnTouchOutside(boolean dismissOnTouchOutside);

}