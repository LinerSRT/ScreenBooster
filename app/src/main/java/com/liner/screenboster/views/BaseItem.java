package com.liner.screenboster.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

public abstract class BaseItem extends LinearLayout {
    protected Context context;
    protected LayoutInflater inflater;

    public BaseItem(Context context) {
        super(context);
        inflater = LayoutInflater.from(context);
        this.context = context;
        onInflate();
        onFindViewById();
    }

    public BaseItem(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        inflater = LayoutInflater.from(context);
        this.context = context;
        onInflate();
        onFindViewById();
    }

    protected abstract void onFindViewById();
    protected abstract void onInflate();

    public void hideView(){
        Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.fade_out);
        animation.setDuration(400);
        startAnimation(animation);
        setVisibility(View.GONE);
    }
    public void showView(){
        Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.fade_in);
        animation.setDuration(400);
        startAnimation(animation);
        setVisibility(View.VISIBLE);
    }
    public void hideView(Animation animation){
        animation.setDuration(400);
        startAnimation(animation);
        setVisibility(View.GONE);
    }
    public void showView(Animation animation){
        animation.setDuration(400);
        startAnimation(animation);
        setVisibility(View.VISIBLE);
    }

    public void hideView(Animation animation, Interpolator interpolator){
        animation.setDuration(400);
        animation.setInterpolator(interpolator);
        startAnimation(animation);
        setVisibility(View.GONE);
    }
    public void showView(Animation animation, Interpolator interpolator){
        animation.setDuration(400);
        animation.setInterpolator(interpolator);
        startAnimation(animation);
        setVisibility(View.VISIBLE);
    }
}