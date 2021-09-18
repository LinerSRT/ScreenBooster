package com.liner.screenboster;

import android.app.Activity;
import android.util.DisplayMetrics;

import com.liner.screenboster.utils.PM;
import com.liner.screenboster.utils.ShellUtils;

public class Display {
    private final int refreshRate;
    private final int width;
    private final int height;
    private float redColor;
    private float greenColor;
    private float blueColor;

    public Display(Activity activity) {
        android.view.Display display = activity.getWindowManager().getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        refreshRate = Math.round(display.getRefreshRate());
        width = metrics.widthPixels;
        height = metrics.heightPixels;
        this.redColor = PM.get("redColor", 1f);
        this.greenColor = PM.get("greenColor", 1f);
        this.blueColor = PM.get("blueColor", 1f);
    }

    public void resetColorMatrix() {
        this.redColor = 1f;
        this.greenColor = 1f;
        this.blueColor = 1f;
        PM.put("redColor", redColor);
        PM.put("greenColor", greenColor);
        PM.put("blueColor", blueColor);
        ShellUtils.execCommand("service call SurfaceFlinger 1015 i32 0", true);
    }

    public void updateColorMatrix(float redColor, float greenColor, float blueColor) {
        this.redColor = redColor;
        this.greenColor = greenColor;
        this.blueColor = blueColor;
        PM.put("redColor", redColor);
        PM.put("greenColor", greenColor);
        PM.put("blueColor", blueColor);
        ShellUtils.execCommand(String.format("service call SurfaceFlinger 1015 i32 1 f %s f 0 f 0 f 0 f 0 f %s f 0 f 0 f 0 f 0 f %s f 0 f 0 f 0 f 0 f 1", redColor, greenColor, blueColor), true);
    }

    public void setSaturation(float saturation){
        ShellUtils.execCommand("service call SurfaceFlinger 1022 f "+saturation, true);
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public int getRefreshRate() {
        return refreshRate;
    }

    public float getRedColor() {
        return redColor;
    }

    public float getGreenColor() {
        return greenColor;
    }

    public float getBlueColor() {
        return blueColor;
    }

    @Override
    public String toString() {
        return "Display{" +
                "refreshRate=" + refreshRate +
                ", width=" + width +
                ", height=" + height +
                ", redColor=" + redColor +
                ", greenColor=" + greenColor +
                ", blueColor=" + blueColor +
                '}';
    }
}
