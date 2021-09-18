package com.liner.screenboster.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.liner.screenboster.R;


public class YSSwitch extends SwitchMaterial {
    public enum FontType{
        BOLD ,
        LIGHT ,
        MEDIUM ,
        REGULAR,
        REGULAR_ITALIC,
        THIN
    }

    private Typeface typeface;

    public YSSwitch(Context context) {
        super(context);
        init(context, null, 0);
    }

    public YSSwitch(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public YSSwitch(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyle) {
        final TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.YSSwitch, defStyle, 0);
        int fontType = typedArray.getInt(R.styleable.YSSwitch_yset_type, 4);
        typedArray.recycle();
        switch (fontType) {
            case 1:
                typeface = Typeface.createFromAsset(context.getAssets(), "yandex_font/Bold.ttf");
                break;
            case 2:
                typeface = Typeface.createFromAsset(context.getAssets(), "yandex_font/Light.ttf");
                break;
            case 3:
                typeface = Typeface.createFromAsset(context.getAssets(), "yandex_font/Medium.ttf");
                break;
            case 4:
                typeface = Typeface.createFromAsset(context.getAssets(), "yandex_font/Regular.ttf");
                break;
            case 5:
                typeface = Typeface.createFromAsset(context.getAssets(), "yandex_font/RegularItalic.ttf");
                break;
            case 6:
                typeface = Typeface.createFromAsset(context.getAssets(), "yandex_font/Thin.ttf");
                break;
        }
        setTypeface(typeface);
    }

    private void setFontType(FontType fontType) {
        switch (fontType){
            case REGULAR:
                typeface = Typeface.createFromAsset(getContext().getAssets(), "yandex_font/Regular.ttf");
                break;
            case BOLD:
                typeface = Typeface.createFromAsset(getContext().getAssets(), "yandex_font/Bold.ttf");
                break;
            case THIN:
                typeface = Typeface.createFromAsset(getContext().getAssets(), "yandex_font/Thin.ttf");
                break;
            case LIGHT:
                typeface = Typeface.createFromAsset(getContext().getAssets(), "yandex_font/Light.ttf");
                break;
            case MEDIUM:
                typeface = Typeface.createFromAsset(getContext().getAssets(), "yandex_font/Medium.ttf");
                break;
            case REGULAR_ITALIC:
                typeface = Typeface.createFromAsset(getContext().getAssets(), "yandex_font/RegularItalic.ttf");
                break;
        }
        setTypeface(typeface);
    }
}
