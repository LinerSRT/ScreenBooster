package com.liner.screenboster;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.animation.AccelerateInterpolator;

import androidx.annotation.NonNull;
import androidx.constraintlayout.utils.widget.ImageFilterView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.liner.screenboster.adapter.GenericAdapter;
import com.liner.screenboster.adapter.binder.LogBinder;
import com.liner.screenboster.adapter.model.LogModel;
import com.liner.screenboster.utils.Device;
import com.liner.screenboster.utils.Shell;
import com.liner.screenboster.utils.ViewUtils;
import com.liner.screenboster.views.BaseDialog;
import com.liner.screenboster.views.BaseDialogBuilder;
import com.liner.screenboster.views.YSTextView;
import com.xw.repo.BubbleSeekBar;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

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
    private MaterialButton rebootButton;
    private RecyclerView logRecycler;
    private GenericAdapter logAdapter;

    private BaseDialog introDialog;
    private BaseDialog readingDTSDialog;
    private DTBOBlock dtboBlock;

    private void findViews() {
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
        rebootButton = findViewById(R.id.rebootButton);
    }

    private void initDialogs(){
        introDialog = new BaseDialogBuilder(this)
                .setDialogTitle("Welcome to ScreenBooster")
                .setDialogText("This program can help you to tweak your display as you want!\nScreenBooster require root-access to proper working.\nIf you do not have them, program will not work.\nGrant root-access for continue")
                .setDialogType(BaseDialogBuilder.Type.WARNING)
                .setAnimationDuration(300)
                .setAnimationInterpolator(new AccelerateInterpolator())
                .setDismissOnTouchOutside(false)
                .build();
        readingDTSDialog = new BaseDialogBuilder(this)
                .setDialogTitle("Reading display specifications")
                .setDialogText("Please wait, program finish reading display specifications")
                .setDialogType(BaseDialogBuilder.Type.INDETERMINATE)
                .setAnimationDuration(300)
                .setAnimationInterpolator(new AccelerateInterpolator())
                .setDismissOnTouchOutside(false)
                .build();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViews();
        initDialogs();
        introDialog.setDialogCancel("I don't have root-access", view -> System.exit(0));
        introDialog.setDialogDone("Grant", view -> {
            if (Shell.haveRoot()) {
                dtboBlock = new DTBOBlock();
                introDialog.closeDialog();
            } else {
                introDialog.setDialogTitleText("Root-access not granted");
                introDialog.setDialogTextText("Sorry, but this program don't support your phone.\nPlease check root-access on your device");
                introDialog.setDialogType(BaseDialogBuilder.Type.ERROR);
                introDialog.setDialogCancel(null, null);
                introDialog.setDialogDone("Exit", view1 -> {
                    System.exit(0);
                });
            }
        });
        introDialog.showDialog();
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
                imageFilterView.setColorFilter(getMatrix());
            }
        });
        displayGreenColorSeek.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListenerAdapter() {
            @Override
            public void onProgressChanged(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat, boolean fromUser) {
                imageFilterView.setColorFilter(getMatrix());
            }
        });
        displayBlueColorSeek.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListenerAdapter() {
            @Override
            public void onProgressChanged(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat, boolean fromUser) {
                imageFilterView.setColorFilter(getMatrix());
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
        refreshSeekBar.setProgress((Math.round(getWindowManager().getDefaultDisplay().getRefreshRate())));
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
                log(LogModel.Type.INFO, "Preparing DTBO block");
                dtboBlock.cleanModFolder();
                log(LogModel.Type.INFO, "Extracting & decompiling DTBO");
                dtboBlock.extract();
                log(LogModel.Type.DONE, "Extracting & decompiling DTBO finished");
                log(LogModel.Type.INFO, "Reading display panel settings");
                DisplaySettings displaySettings = dtboBlock.getDisplaySettings();
                log(LogModel.Type.DONE, "Reading display panel settings finished");
                displaySettings.setPanelFramerate(refreshSeekBar.getProgress());
                log(LogModel.Type.INFO, "Writing display panel settings");
                dtboBlock.writeDisplaySettings(displaySettings);
                log(LogModel.Type.DONE, "Writing display panel settings finished");
                log(LogModel.Type.INFO, "Compressing & compiling DTBO");
                dtboBlock.compile();
                log(LogModel.Type.DONE, "Compressing & compiling DTBO finished");
                log(LogModel.Type.INFO, "Writing modded DTBO");
                dtboBlock.write("/sdcard/dtbo_mod.img");
                log(LogModel.Type.DONE, "Writing modded DTBO finished");
            }
        });
        rebootButton.setOnClickListener(view -> Shell.exec("reboot", true));
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


    private void updateRefreshUI(int refreshRate) {
        refreshValueText.setText(String.format("%s HZ", refreshRate));
        float percent = (((float) refreshRate - 60) / (refreshSeekBar.getMax() - 60));
        int color = ViewUtils.interpolateColor(ContextCompat.getColor(this, R.color.textColor), Color.RED, percent);
        refreshValueText.setTextColor(color);
    }

    private void log(LogModel.Type type, String text) {
        runOnUiThread(() -> {
            logAdapter.add(new LogModel(new SimpleDateFormat("HH:mm:ss").format(new Date()), type, text));
            logAdapter.notifyItemInserted(logAdapter.getItems().size() - 1);
            logRecycler.scrollToPosition(logAdapter.getItems().size() - 1);
        });
    }


    private ColorMatrixColorFilter getMatrix() {
        return new ColorMatrixColorFilter(new ColorMatrix(new float[]{
                ((displayRedColorSeek.getProgress()) / 100f), 0, 0, 0, 0,
                0, ((displayGreenColorSeek.getProgress()) / 100f), 0, 0, 0,
                0, 0, ((displayBlueColorSeek.getProgress()) / 100f), 0, 0,
                0, 0, 0, 1, 0}));
    }
}