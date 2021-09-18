package com.liner.screenboster;

import android.app.Application;

import com.liner.screenboster.utils.PM;

public class Core extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        PM.init(this);
    }
}
