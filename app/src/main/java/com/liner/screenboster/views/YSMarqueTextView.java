package com.liner.screenboster.views;

import android.content.Context;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.AttributeSet;


public class YSMarqueTextView extends YSTextView {

    public YSMarqueTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public YSMarqueTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public YSMarqueTextView(Context context) {
        super(context);
        init();
    }

    private void init(){
        setEllipsize(TextUtils.TruncateAt.MARQUEE);
        setMarqueeRepeatLimit(-1);
        setHorizontallyScrolling(true);
        setSingleLine(true);
    }

    public boolean isFocused() {
        return true;
    }

    protected void onFocusChanged(boolean paramBoolean, int paramInt, Rect paramRect) {
        super.onFocusChanged(paramBoolean,paramInt,paramRect);
    }
}
