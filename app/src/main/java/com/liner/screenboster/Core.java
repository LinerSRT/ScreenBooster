package com.liner.screenboster;

import android.annotation.SuppressLint;
import android.app.Application;

import com.liner.screenboster.utils.Assets;
import com.liner.screenboster.utils.PM;
import com.liner.screenboster.utils.Shell;

public class Core extends Application {
    @SuppressLint("SdCardPath")
    public static String DIRECTORY_ROOT = "/data/data/com.liner.screenboster/files/";
    public static String DIRECTORY_TOOLS = DIRECTORY_ROOT + "tools/";
    public static String DIRECTORY_STOCK_DTBO = DIRECTORY_ROOT + "stock_dtbo/";
    public static String DIRECTORY_MOD_DTBO = DIRECTORY_ROOT + "modded_dtbo/";
    public static String DIRECTORY_STOCK_DTB = DIRECTORY_STOCK_DTBO + "dtb/";
    public static String DIRECTORY_STOCK_DTS = DIRECTORY_STOCK_DTBO + "dts/";
    public static String DIRECTORY_MOD_DTB = DIRECTORY_MOD_DTBO + "dtb/";
    public static String DIRECTORY_MOD_DTS = DIRECTORY_MOD_DTBO + "dts/";

    @Override
    public void onCreate() {
        super.onCreate();
        PM.init(this);
        Shell.exec("mkdir -p " + DIRECTORY_TOOLS, true);
        Shell.exec("mkdir -p " + DIRECTORY_STOCK_DTB, true);
        Shell.exec("mkdir -p " + DIRECTORY_MOD_DTB, true);
        Shell.exec("mkdir -p " + DIRECTORY_STOCK_DTS, true);
        Shell.exec("mkdir -p " + DIRECTORY_MOD_DTS, true);
        Assets.copy(this, "imgtool", DIRECTORY_TOOLS + "imgtool.android.arm64");
        Assets.copy(this, "dtc", DIRECTORY_TOOLS + "dtc");
        Assets.copy(this, "mkdtimg", DIRECTORY_TOOLS + "mkdtimg");
        Shell.exec("chmod 777 -R " + DIRECTORY_ROOT, true);
    }
}
