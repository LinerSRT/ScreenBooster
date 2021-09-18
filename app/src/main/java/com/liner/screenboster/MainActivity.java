package com.liner.screenboster;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.constraintlayout.utils.widget.ImageFilterView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.liner.screenboster.adapter.GenericAdapter;
import com.liner.screenboster.adapter.binder.LogBinder;
import com.liner.screenboster.adapter.model.LogModel;
import com.liner.screenboster.utils.Assets;
import com.liner.screenboster.utils.Device;
import com.liner.screenboster.utils.ShellUtils;
import com.liner.screenboster.utils.ViewUtils;
import com.liner.screenboster.views.YSTextView;
import com.xw.repo.BubbleSeekBar;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressLint("SimpleDateFormat")
public class MainActivity extends LActivity {
    private Display display;

    private YSTextView deviceName;
    private YSTextView screenSpecification;
    private YSTextView refreshValueText;
    private ImageFilterView imageFilterView;
    private BubbleSeekBar displaySaturationSeek;
    private BubbleSeekBar displayRedColorSeek;
    private BubbleSeekBar displayGreenColorSeek;
    private BubbleSeekBar displayBlueColorSeek;
    private BubbleSeekBar refreshSeekBar;
    private MaterialButton displayRestoreColorMode;
    private MaterialButton displayModifyColor;
    private MaterialButton applyRefreshRateButton;
    private RecyclerView logRecycler;
    private GenericAdapter logAdapter;


//
//
//    private YSTextView refreshValueText;
//    private MaterialButton applyRefreshRateButton;
//    private String dtboBlock;
//
//    private BubbleSeekBar refreshSeekBar;
//    private BubbleSeekBar phantomCorrection;
//    private BubbleSeekBar screenRedColor;
//
//
//
//    private BaseDialog rootAccessDialog;
//    private BaseDialog rootDeniedDialog;
//    private boolean rootAccessGranted = false;
//    private RecyclerView programLog;
//    private GenericAdapter genericAdapter;
    private DTBO dtbo;
//    private Display display;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        deviceName = findViewById(R.id.deviceName);
        screenSpecification = findViewById(R.id.screenSpecification);
        displaySaturationSeek = findViewById(R.id.displaySaturationSeek);
        displayRedColorSeek = findViewById(R.id.displayRedColorSeek);
        displayGreenColorSeek = findViewById(R.id.displayGreenColorSeek);
        displayBlueColorSeek = findViewById(R.id.displayBlueColorSeek);
        displayRestoreColorMode = findViewById(R.id.displayRestoreColorMode);
        displayModifyColor = findViewById(R.id.displayModifyColor);
        imageFilterView = findViewById(R.id.imageView);
        applyRefreshRateButton = findViewById(R.id.applyRefreshRateButton);
        refreshValueText = findViewById(R.id.refreshValueText);
        refreshSeekBar = findViewById(R.id.refreshSeekBar);
        logRecycler = findViewById(R.id.logRecycler);
        display = new Display(this);
        logAdapter = new GenericAdapter();
        logRecycler.setAdapter(logAdapter);
        logRecycler.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        logAdapter.register(R.layout.log_adapter_holder, LogModel.class, LogBinder.class);
        deviceName.setText(Device.getDeviceName());
        screenSpecification.setText(String.format("%sx%s @ %sHz", display.getWidth(), display.getHeight(), Math.round(display.getRefreshRate())));
        displaySaturationSeek.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListenerAdapter() {
            @Override
            public void onProgressChanged(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat, boolean fromUser) {
                imageFilterView.setSaturation(((progressFloat)) / 100.0f);
            }
        });
        displayRedColorSeek.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListenerAdapter() {
            @Override
            public void onProgressChanged(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat, boolean fromUser) {
                imageFilterView.setColorFilter(new ColorMatrixColorFilter(new ColorMatrix(new float[]{
                        ((displayRedColorSeek.getProgress()) / 100f), 0, 0, 0, 0,
                        0, ((displayGreenColorSeek.getProgress()) / 100f), 0, 0, 0,
                        0, 0, ((displayBlueColorSeek.getProgress()) / 100f), 0, 0,
                        0, 0, 0, 1, 0})));
            }
        });
        displayGreenColorSeek.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListenerAdapter() {
            @Override
            public void onProgressChanged(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat, boolean fromUser) {
                imageFilterView.setColorFilter(new ColorMatrixColorFilter(new ColorMatrix(new float[]{
                        ((displayRedColorSeek.getProgress()) / 100f), 0, 0, 0, 0,
                        0, ((displayGreenColorSeek.getProgress()) / 100f), 0, 0, 0,
                        0, 0, ((displayBlueColorSeek.getProgress()) / 100f), 0, 0,
                        0, 0, 0, 1, 0})));
            }
        });
        displayBlueColorSeek.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListenerAdapter() {
            @Override
            public void onProgressChanged(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat, boolean fromUser) {
                imageFilterView.setColorFilter(new ColorMatrixColorFilter(new ColorMatrix(new float[]{
                        ((displayRedColorSeek.getProgress()) / 100f), 0, 0, 0, 0,
                        0, ((displayGreenColorSeek.getProgress()) / 100f), 0, 0, 0,
                        0, 0, ((displayBlueColorSeek.getProgress()) / 100f), 0, 0,
                        0, 0, 0, 1, 0})));
            }
        });
        displayRestoreColorMode.setOnClickListener(view -> {
            displaySaturationSeek.setProgress(100);
            displayRedColorSeek.setProgress(100);
            displayGreenColorSeek.setProgress(100);
            displayBlueColorSeek.setProgress(100);
            display.setSaturation(1f);
            display.resetColorMatrix();
        });
        displayModifyColor.setOnClickListener(view -> {
            display.setSaturation(((displaySaturationSeek.getProgress())) / 100.0f);
            display.updateColorMatrix(((displayRedColorSeek.getProgress()) / 100f), ((displayGreenColorSeek.getProgress()) / 100f), ((displayBlueColorSeek.getProgress()) / 100f));
            imageFilterView.setColorFilter(new ColorMatrixColorFilter(new ColorMatrix(new float[]{
                    1, 0, 0, 0, 0,
                    0, 1, 0, 0, 0,
                    0, 0, 1, 0, 0,
                    0, 0, 0, 1, 0})));
            imageFilterView.setSaturation(1);
        });
        updateRefreshUI(Math.round(getWindowManager().getDefaultDisplay().getRefreshRate()));
        refreshSeekBar.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onProgressChanged(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat, boolean fromUser) {
                updateRefreshUI(progress);
            }

            @Override
            public void getProgressOnActionUp(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {

            }

            @Override
            public void getProgressOnFinally(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat, boolean fromUser) {

            }
        });
        refreshSeekBar.setCustomSectionTextArray(new BubbleSeekBar.CustomSectionTextArray() {
            @NonNull
            @Override
            public SparseArray<String> onCustomize(int sectionCount, @NonNull SparseArray<String> array) {
                array.clear();
                array.put(0, "Safe");
                array.put(1, "Unsafe");
                array.put(3, "Danger");
                return array;
            }
        });
        applyRefreshRateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dtbo = new DTBO();
                dtbo.extract(MainActivity.this, new DTBO.Operation() {
                    @Override
                    public void onStart() {
                        log(LogModel.Type.INFO, "Preparing DTBO block");
                        log(LogModel.Type.INFO, "Extracting & decompiling DTBO");
                    }

                    @Override
                    public void onFinished() {
                        log(LogModel.Type.DONE, "Extracting & decompiling DTBO finished");
                        dtbo.readDisplaySettings(MainActivity.this, new DTBO.DTSReadDisplayOperation() {
                            @Override
                            public void onStart() {
                                log(LogModel.Type.INFO, "Reading display panel settings");
                            }

                            @Override
                            public void onFinished(List<DTSDisplaySettings> displaySettingsList) {
                                if(displaySettingsList.isEmpty()){
                                    log(LogModel.Type.WARN, "Not fond any settings in DTS!");
                                } else {
                                    DTSDisplaySettings displaySettings = displaySettingsList.get(0);

                                    dtbo.writeDisplayFrameRate(MainActivity.this, refreshSeekBar.getProgress(), new DTBO.Operation() {
                                        @Override
                                        public void onStart() {
                                            log(LogModel.Type.INFO, "Writing "+refreshSeekBar.getProgress()+" to DTS settings");
                                        }

                                        @Override
                                        public void onFinished() {
                                            log(LogModel.Type.DONE, "Writing DTS settings finished");
                                            dtbo.compress(MainActivity.this, new DTBO.Operation() {
                                                @Override
                                                public void onStart() {
                                                    log(LogModel.Type.INFO, "Start compiling DTS files to DTBO");
                                                }

                                                @Override
                                                public void onFinished() {
                                                    log(LogModel.Type.DONE, "Compiling DTS finished, modded DTBO = sdcard/dtbo_modded.img");
                                                    dtbo.write("sdcard/dtbo_modded.img");
                                                }

                                                @Override
                                                public void onFailed() {
                                                    log(LogModel.Type.ERROR, "Compilation DTS failed");
                                                }
                                            });
                                        }

                                        @Override
                                        public void onFailed() {
                                            log(LogModel.Type.ERROR, "Failed to write display panel settings");
                                        }
                                    });
                                }
                            }

                            @Override
                            public void onFailed() {
                                log(LogModel.Type.ERROR, "Failed to read display panel settings");
                            }
                        });
                    }

                    @Override
                    public void onFailed() {
                        log(LogModel.Type.ERROR, "Failed execution DTBO functions");
                    }
                });
            }
        });

//
//        rootAccessGranted = ShellUtils.checkRootPermission();
//
//        applyRefreshRateButton = ViewUtils.findById(this, R.id.applyRefreshRateButton);
//        phantomCorrection = ViewUtils.findById(this, R.id.displaySaturationSeek);
//        refreshValueText = ViewUtils.findById(this, R.id.refreshValueText);
//        refreshSeekBar = ViewUtils.findById(this, R.id.refreshSeekBar);
//        programLog = ViewUtils.findById(this, R.id.programLog);
//        deviceName = ViewUtils.findById(this, R.id.deviceName);
//        screenSpecs = ViewUtils.findById(this, R.id.screenSpecification);
//        screenRedColor = ViewUtils.findById(this, R.id.screenRedColor);
//        deviceName.setText(Device.getDeviceName());
//
//        genericAdapter = new GenericAdapter();
//        programLog.setAdapter(genericAdapter);
//        programLog.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
//        genericAdapter.register(R.layout.log_adapter_holder, LogModel.class, LogBinder.class);
//        screenSpecs.setText(String.format("%sx%s @ %sHz", display.getWidth(), display.getHeight(), Math.round(display.getRefreshRate())));
//
//
//
//
//
//        rootAccessDialog = new BaseDialogBuilder(this)
//                .setDialogTitle("This application require ROOT access")
//                .setDialogText("To properly work this application need to be granted ROOT access. Its will help modify DTBO block in device properly.")
//                .setAnimationInterpolator(new AccelerateInterpolator())
//                .setDismissOnTouchOutside(false)
//                .setDialogType(BaseDialogBuilder.Type.WARNING)
//                .setAnimationDuration(300)
//                .build();
//        rootDeniedDialog = new BaseDialogBuilder(this)
//                .setDialogTitle("Your phone dont have ROOT access")
//                .setDialogText("Sorry, but this application cannot work on your device. Program will be exit")
//                .setAnimationInterpolator(new AccelerateInterpolator())
//                .setDismissOnTouchOutside(false)
//                .setDialogType(BaseDialogBuilder.Type.ERROR)
//                .setAnimationDuration(300)
//                .build();
//        rootDeniedDialog.setDialogDone("Ok", view -> finish());
//        rootAccessDialog.setDialogCancel("Exit", view -> finish());
//        rootAccessDialog.setDialogDone("Grant", view -> {
//            rootAccessGranted = ShellUtils.checkRootPermission();
//            if(!rootAccessGranted){
//                rootAccessDialog.closeDialog();
//            } else {
//                rootAccessDialog.closeDialog();
//                rootDeniedDialog.showDialog();
//            }
//        });
//        if(!rootAccessGranted){
//            rootAccessDialog.showDialog();
//        } else {
//            dtbo = new DTBO();
//            log(LogModel.Type.INFO, "Clearing working directory");
//            ShellUtils.CommandResult commandResult = ShellUtils.execCommand("rm -rf "+getFilesDir()+"/*", true);
//            if(commandResult.isSuccess()){
//                log(LogModel.Type.DONE, "Clearing working directory finished");
//            } else {
//                log(LogModel.Type.ERROR, "Failed to clear working directory");
//            }
//            log(LogModel.Type.INFO, "Unpacking tools to working directory");
//            Assets.copy(this, "mkdtimg", "mkdtimg");
//            Assets.copy(this, "dtc", "dtc");
//            Assets.copy(this, "imgtool", "imgtool.android.arm64");
//            Assets.copy(this, "build", "build.sh");
//            Assets.copy(this, "dtc", "dtc");
//            Assets.copy(this, "mkdir", "mkdir.sh");
//            Assets.copy(this, "dec", "dec.sh");
//            log(LogModel.Type.DONE, "Tools unpacked success");
//            log(LogModel.Type.INFO, "Fixing permissions");
//            commandResult = ShellUtils.execCommand(
//                    "chmod 777 -R /data/data/" + getPackageName() + "/files/*",
//                    true
//            );
//            if(commandResult.isSuccess()){
//                log(LogModel.Type.DONE, "Permissions fix complete");
//            } else {
//                log(LogModel.Type.ERROR, "Failed to modify permissions in working directory");
//            }
//            log(LogModel.Type.INFO, "Creating backup's");
//            dtbo.backup(new DTBO.Operation() {
//                @Override
//                public void onFinished() {
//                    log(LogModel.Type.DONE, "DTBO block backup done, image saved to sdcard/dtbo_backup.img");
//                }
//
//                @Override
//                public void onFailed() {
//                    log(LogModel.Type.ERROR, "Failed to create DTBO block backup!");
//                }
//            });
//        }
//
//
//        phantomCorrection.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListener() {
//            @SuppressLint("SetTextI18n")
//            @Override
//            public void onProgressChanged(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat, boolean fromUser) {
//                ShellUtils.execCommand("service call SurfaceFlinger 1022 f "+((progressFloat * 2)) / 100.0f, true);
//            }
//
//            @Override
//            public void getProgressOnActionUp(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {
//                ShellUtils.execCommand("setprop persist.sys.sf.color_saturation "+((progressFloat * 2)) / 100.0f, true);
//            }
//
//            @Override
//            public void getProgressOnFinally(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat, boolean fromUser) {
//
//            }
//        });
//        screenRedColor.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListener() {
//            @SuppressLint("SetTextI18n")
//            @Override
//            public void onProgressChanged(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat, boolean fromUser) {
//                ShellUtils.execCommand(String.format("service call SurfaceFlinger 1015 i32 1 f 1 f 0 f 0 f 0 f 0 f %s f 0 f 0 f 0 f 0 f 0.8 f 0 f 0 f 0 f 0 f 1", (progress/100f)), true);
//            }
//
//            @Override
//            public void getProgressOnActionUp(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {
//
//            }
//
//            @Override
//            public void getProgressOnFinally(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat, boolean fromUser) {
//
//            }
//        });
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//

//
//        applyRefreshRateButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                appendLog("Extracting tools...");
//                Assets.copy(MainActivity.this, "mkdtimg", "mkdtimg");
//                Assets.copy(MainActivity.this, "dtc", "dtc");
//                Assets.copy(MainActivity.this, "imgtool", "imgtool.android.arm64");
//                Assets.copy(MainActivity.this, "build", "build.sh");
//                Assets.copy(MainActivity.this, "dtc", "dtc");
//                Assets.copy(MainActivity.this, "mkdir", "mkdir.sh");
//                Assets.copy(MainActivity.this, "dec", "dec.sh");
//                //if (!TextUtils.isEmpty(refreshRateValueEdit.getText().toString()))
//                //    modifyDTBO(Integer.parseInt(refreshRateValueEdit.getText().toString()), Math.round(getWindowManager().getDefaultDisplay().getRefreshRate()));
//            }
//        });


    }

    @Override
    public int getStatusBarColor() {
        return ContextCompat.getColor(this, R.color.backgroundSecondaryColor);
    }

    @Override
    public int getNavigationBarColor() {
        return ContextCompat.getColor(this, R.color.backgroundColor);
    }

    @Override
    public void onBackPressed() {
        finish();
    }


    private void modifyDTBO(@IntRange(from = 60, to = 120) int refreshRate, int oldRefreshRate) {
//        appendLog("Searching DTBO block address...");
//        new Thread(() -> {
//            ShellUtils.CommandResult detectDTBOCommand = ShellUtils.execCommand(
//                    "cd /dev/block/by-name;ls -l dtbo",
//                    true,
//                    true
//            );
//            if (detectDTBOCommand.result == 0) {
//                dtboBlock = detectDTBOCommand.successMsg.substring(detectDTBOCommand.successMsg.indexOf("/"));
//                appendLog("Found DTBO block at: \"" + dtboBlock + "\"");
//                appendLog("Reading to: \"sdcard/dtbo.img\" from: \"" + dtboBlock + "\"");
//                ShellUtils.CommandResult readDTBOCommand = ShellUtils.execCommand(
//                        "dd if=" + dtboBlock + " of=sdcard/dtbo.img",
//                        true,
//                        true
//                );
//                if (readDTBOCommand.result == 0) {
//                    appendLog("Creating workspace");
//                    ShellUtils.CommandResult clearDTBOBuilds = ShellUtils.execCommand(
//                            "chmod 777 -R /data/data/" + getPackageName() + "/files/;sh /data/data/" + getPackageName() + "/files/build.sh",
//                            true,
//                            true
//                    );
//                    if (clearDTBOBuilds.result == 0) {
//                        appendLog("Decompiling & Patching DTBO");
//                        ShellUtils.CommandResult decompileDTBOCommand = ShellUtils.execCommand(
//                                "chmod 777 -R /data/data/" + getPackageName() + "/files/;sh /data/data/" + getPackageName() + "/files/mkdir.sh ",
//                                true,
//                                true
//                        );
//                        if (decompileDTBOCommand.result == 0) {
//                            appendLog("Recompiling DTBO");
//                            ShellUtils.CommandResult patchDTBOCommand = ShellUtils.execCommand(
//                                    "chmod 777 -R /data/data/" + getPackageName() + "/files/;sh /data/data/" + getPackageName() + "/files/dec.sh " + refreshRate + " " + oldRefreshRate,
//                                    true,
//                                    true
//                            );
//                            if (patchDTBOCommand.result == 0) {
//                                appendLog("DTBO recompiled successfully");
//                                appendLog("Writing DTBO block");
//                                ShellUtils.CommandResult writeDTBO = ShellUtils.execCommand(
//                                        "dd if=sdcard/dtbo_modified.img of=" + dtboBlock,
//                                        true,
//                                        true
//                                );
//                                if (writeDTBO.result != 0) {
//                                    appendLog("Writing DTBO block finished");
//                                } else {
//                                    appendLog(writeDTBO.errorMsg);
//                                }
//
//                                ShellUtils.execCommand("reboot", true, true);
//
////                                this.ifPath = "sdcard/dtbo_" + refreshRate + ".img";
////                                this.ofPath = this.dtbo;
////                                r0 = new Runnable() {
////                                    public final void run() {
////                                        MainActivity.this.lambda$main$10$MainActivity();
////                                    }
////                                };
////                                runOnUiThread(r0);
//                            }
//                        }
//
//
//                    }
//                }
//            }
//
//        }).start();

    }



    private void updateRefreshUI(int refreshRate) {
        refreshValueText.setText(String.format("%s HZ", refreshRate));
        float percent = (((float) refreshRate - 60) / (refreshSeekBar.getMax() - 60));
        int color = ViewUtils.interpolateColor(ContextCompat.getColor(this, R.color.textColor), Color.RED, percent);
        refreshValueText.setTextColor(color);
    }

    private void log(LogModel.Type type, String text){
        runOnUiThread(() -> {
            logAdapter.add(new LogModel(new SimpleDateFormat("HH:mm:ss").format(new Date()), type, text));
            logAdapter.notifyItemInserted(logAdapter.getItems().size()-1);
            logRecycler.scrollToPosition(logAdapter.getItems().size()-1);
        });
    }
}