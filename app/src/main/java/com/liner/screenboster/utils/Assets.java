package com.liner.screenboster.utils;

import android.content.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class Assets {
    public static void copy(Context context, String assetPath, String outPath) {
        File file = new File(context.getFilesDir(), outPath);
        try {
            InputStream inputStream = context.getAssets().open(assetPath);
            FileOutputStream outputStream = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            int read;
            while((read = inputStream.read(buffer)) != -1){
                outputStream.write(buffer, 0, read);
            }
            inputStream.close();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
