package com.liner.screenboster;

import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.IdRes;
import androidx.appcompat.app.AppCompatActivity;

import com.liner.screenboster.utils.ViewUtils;

public abstract class LActivity extends AppCompatActivity {
    protected static String TAG = "TAGTAG";

    @Override
    protected void onStart() {
        super.onStart();
        ViewUtils.setStatusBarColor(this, getStatusBarColor());
        ViewUtils.setNavigationBarColor(this, getNavigationBarColor());
    }

    @ColorInt
    public abstract int getStatusBarColor();
    @ColorInt
    public abstract int getNavigationBarColor();
}
