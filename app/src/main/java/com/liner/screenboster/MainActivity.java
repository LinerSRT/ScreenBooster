package com.liner.screenboster;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.os.Bundle;
import android.view.animation.OvershootInterpolator;

import androidx.constraintlayout.utils.widget.ImageFilterView;
import androidx.core.content.ContextCompat;

import com.google.android.material.button.MaterialButton;
import com.liner.screenboster.utils.PM;
import com.liner.screenboster.utils.Shell;
import com.liner.screenboster.utils.ViewUtils;
import com.liner.screenboster.views.BaseDialog;
import com.liner.screenboster.views.BaseDialogBuilder;
import com.liner.screenboster.views.YSTextView;
import com.liner.screenboster.views.coloredtextview.ColoredTextView;
import com.xw.repo.BubbleSeekBar;


@SuppressLint("SimpleDateFormat")
public class MainActivity extends LActivity {
    private Display display;
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
    private MaterialButton restoreDTBO;

    private BaseDialog introDialog;
    private BaseDialog readingDTSDialog;
    private BaseDialog modifyingDTSDialog;
    private BaseDialog modifyingDTSFinishDialog;
    private BaseDialog noDTBOBlockDialog;
    private DTBOBlock dtboBlock;


    private ColoredTextView displaySize;
    private ColoredTextView displayName;
    private ColoredTextView displayFramerate;
    private ColoredTextView displayHFrontPorch;
    private ColoredTextView displayHBackPorch;
    private ColoredTextView displayHPulseWidth;
    private ColoredTextView displayVFrontPorch;
    private ColoredTextView displayVBackPorch;
    private ColoredTextView displayVPulseWidth;
    private ColoredTextView displaySyncSkew;

    private void findViews() {
        displaySize = findViewById(R.id.displaySize);
        displayName = findViewById(R.id.displayName);
        displayFramerate = findViewById(R.id.displayFramerate);
        displayHFrontPorch = findViewById(R.id.displayHFrontPorch);
        displayHBackPorch = findViewById(R.id.displayHBackPorch);
        displayHPulseWidth = findViewById(R.id.displayHPulseWidth);
        displayVFrontPorch = findViewById(R.id.displayVFrontPorch);
        displayVBackPorch = findViewById(R.id.displayVBackPorch);
        displayVPulseWidth = findViewById(R.id.displayVPulseWidth);
        displaySyncSkew = findViewById(R.id.displaySyncSkew);
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
        restoreDTBO = findViewById(R.id.restoreDTBO);
    }

    private void initDialogs() {
        introDialog = new BaseDialogBuilder(this)
                .setDialogTitle(getString(R.string.welcomeDialog))
                .setDialogText(getString(R.string.welcomeDialogText))
                .setDialogType(BaseDialogBuilder.Type.WARNING)
                .setAnimationDuration(300)
                .setAnimationInterpolator(new OvershootInterpolator())
                .setDismissOnTouchOutside(false)
                .build();
        readingDTSDialog = new BaseDialogBuilder(this)
                .setDialogTitle(getString(R.string.readingDisplay))
                .setDialogText(getString(R.string.pleaseWaitWhileReadingDisplay))
                .setDialogType(BaseDialogBuilder.Type.INDETERMINATE)
                .setAnimationDuration(300)
                .setAnimationInterpolator(new OvershootInterpolator())
                .setDismissOnTouchOutside(false)
                .build();
        modifyingDTSDialog = new BaseDialogBuilder(this)
                .setDialogTitle(getString(R.string.waitWhileApplyChanges))
                .setDialogText("")
                .setDialogType(BaseDialogBuilder.Type.INDETERMINATE)
                .setAnimationDuration(300)
                .setAnimationInterpolator(new OvershootInterpolator())
                .setDismissOnTouchOutside(false)
                .build();
        modifyingDTSFinishDialog = new BaseDialogBuilder(this)
                .setDialogTitle(getString(R.string.modifyingDTBOFinished))
                .setDialogText(getString(R.string.rebootAsk))
                .setDialogType(BaseDialogBuilder.Type.INFO)
                .setAnimationDuration(300)
                .setAnimationInterpolator(new OvershootInterpolator())
                .setDismissOnTouchOutside(false)
                .build();

        noDTBOBlockDialog = new BaseDialogBuilder(this)
                .setDialogTitle(getString(R.string.deviceNotSupported))
                .setDialogText(getString(R.string.deviceNotSupportDescription))
                .setDialogType(BaseDialogBuilder.Type.ERROR)
                .setAnimationDuration(300)
                .setAnimationInterpolator(new OvershootInterpolator())
                .setDismissOnTouchOutside(false)
                .build();
        noDTBOBlockDialog.setDialogDone(getString(R.string.exit), view -> {
            System.exit(0);
        });
        modifyingDTSFinishDialog.setDialogCancel(getString(R.string.no), view -> modifyingDTSFinishDialog.closeDialog());
        modifyingDTSFinishDialog.setDialogDone(getString(R.string.reboot), view -> Shell.exec("reboot", true));
    }

    private void initializeDTS() {
        PM.put("intro_show", true);
        introDialog.closeDialog();
        readingDTSDialog.showDialog();
        new Thread(() -> {
            dtboBlock = new DTBOBlock();
            if (dtboBlock.isHaveBlock()) {
                display = new Display(MainActivity.this);
                int primaryColor = ContextCompat.getColor(MainActivity.this, R.color.primaryColor);
                DisplaySettings displaySettings = dtboBlock.getDisplaySettings();
                runOnUiThread(() -> {
                    displaySize.setText(String.format(getString(R.string.displaySize), displaySettings.getPanelWidth() + "x" + displaySettings.getPanelHeight()));
                    displayName.setText(String.format(getString(R.string.displayName), displaySettings.getPanelName()));
                    displayFramerate.setText(String.format(getString(R.string.displayFramerate), displaySettings.getPanelFramerate() + " Hz"));
                    displayHFrontPorch.setText(String.format(getString(R.string.displayHFPorch), String.valueOf(displaySettings.gethFrontPorch())));
                    displayHBackPorch.setText(String.format(getString(R.string.displayHBPorch), String.valueOf(displaySettings.gethBackPorch())));
                    displayHPulseWidth.setText(String.format(getString(R.string.displayHBPorch), String.valueOf(displaySettings.gethPulseWidth())));
                    displayVFrontPorch.setText(String.format(getString(R.string.displayVFPorch), String.valueOf(displaySettings.getvFrontPorch())));
                    displayVBackPorch.setText(String.format(getString(R.string.displayVBPorch), String.valueOf(displaySettings.getvBackPorch())));
                    displayVPulseWidth.setText(String.format(getString(R.string.displayVBPorch), String.valueOf(displaySettings.getvPulseWidth())));
                    displaySyncSkew.setText(String.format(getString(R.string.displaySync), String.valueOf(displaySettings.gethSyncSkew())));
                    readingDTSDialog.closeDialog();
                });
            } else {
                runOnUiThread(() -> {
                    readingDTSDialog.closeDialog();
                    noDTBOBlockDialog.showDialog();
                });
            }
        }).start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViews();
        displaySize.setText(String.format(getString(R.string.displaySize), "-"));
        displayName.setText(String.format(getString(R.string.displayName), "-"));
        displayFramerate.setText(String.format(getString(R.string.displayFramerate), "-"));
        displayHFrontPorch.setText(String.format(getString(R.string.displayHFPorch), "-"));
        displayHBackPorch.setText(String.format(getString(R.string.displayHBPorch), "-"));
        displayHPulseWidth.setText(String.format(getString(R.string.displayHBPorch), "-"));
        displayVFrontPorch.setText(String.format(getString(R.string.displayVFPorch), "-"));
        displayVBackPorch.setText(String.format(getString(R.string.displayVBPorch), "-"));
        displayVPulseWidth.setText(String.format(getString(R.string.displayVBPorch), "-"));
        displaySyncSkew.setText(String.format(getString(R.string.displaySync), "-"));
        initDialogs();
        introDialog.setDialogCancel(getString(R.string.noRootButton), view -> System.exit(0));
        introDialog.setDialogDone(getString(R.string.grantRootButton), view -> {
            if (Shell.haveRoot()) {
                initializeDTS();
            } else {
                introDialog.setDialogTitleText(getString(R.string.rootNotGranted));
                introDialog.setDialogTextText(getString(R.string.rootRequireDescription));
                introDialog.setDialogType(BaseDialogBuilder.Type.ERROR);
                introDialog.setDialogCancel(null, null);
                introDialog.setDialogDone(getString(R.string.exit), view1 -> {
                    System.exit(0);
                });
            }
        });
        if (!(Boolean) PM.get("intro_show", false)) {
            introDialog.showDialog();
        } else {
            initializeDTS();
        }
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
        refreshSeekBar.setCustomSectionTextArray((sectionCount, array) -> {
            array.clear();
            array.put(0, getString(R.string.safe));
            array.put(1, getString(R.string.unsafe));
            array.put(3, getString(R.string.danger));
            return array;
        });
        applyRefreshRateButton.setOnClickListener(view -> {
            modifyingDTSDialog.showDialog();
            log(getString(R.string.prepareDTBO));
            new Thread(() -> {
                dtboBlock.cleanModFolder();
                log(getString(R.string.extractDTBO));
                dtboBlock.extract();
                log(getString(R.string.extractDTBOFinished));
                log(getString(R.string.readPanelSettings));
                DisplaySettings displaySettings = dtboBlock.getDisplaySettings();
                log(getString(R.string.readPanelSettingsFinished));
                displaySettings.setPanelFramerate(refreshSeekBar.getProgress());
                log(getString(R.string.writePanelSettings));
                dtboBlock.writeDisplaySettings(displaySettings);
                log(getString(R.string.writePanelSettingsFinished));
                log(getString(R.string.compressDTBO));
                dtboBlock.compile();
                log(getString(R.string.compressDTBOFinished));
                log(getString(R.string.writingDTBO));
                dtboBlock.write("/sdcard/dtbo_mod.img");
                log(getString(R.string.writingDTBOFinished));
                runOnUiThread(() -> {
                    modifyingDTSDialog.closeDialog();
                    modifyingDTSFinishDialog.showDialog();
                });
            }).start();
        });
        restoreDTBO.setOnClickListener(view -> dtboBlock.write("/sdcard/dtbo_backup.img"));
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

    private void log(String text) {
        runOnUiThread(() -> modifyingDTSDialog.setDialogTextText(text));
    }


    private ColorMatrixColorFilter getMatrix() {
        return new ColorMatrixColorFilter(new ColorMatrix(new float[]{
                ((displayRedColorSeek.getProgress()) / 100f), 0, 0, 0, 0,
                0, ((displayGreenColorSeek.getProgress()) / 100f), 0, 0, 0,
                0, 0, ((displayBlueColorSeek.getProgress()) / 100f), 0, 0,
                0, 0, 0, 1, 0}));
    }
}