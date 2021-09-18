package com.liner.screenboster.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.ImageView;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.constraintlayout.utils.widget.ImageFilterView;

public class ColorImageView extends AppCompatImageView {
    private ColorFilter colorFilter;
    private Paint paint;

    public ColorImageView(Context context) {
        super(context);
        colorFilter = new ColorFilter();
        paint = new Paint();
    }

    public ColorImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        colorFilter = new ColorFilter();
        paint = new Paint();
    }

}
