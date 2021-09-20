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
import com.liner.screenboster.views.coloredtextview.Span;
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
    private MaterialButton rebootButton;
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
        rebootButton = findViewById(R.id.rebootButton);
        restoreDTBO = findViewById(R.id.restoreDTBO);
    }

    private void initDialogs() {
        introDialog = new BaseDialogBuilder(this)
                .setDialogTitle("Welcome to ScreenBooster")
                .setDialogText("This program can help you to tweak your display as you want! ScreenBooster require root-access to proper working. If you do not have them, program will not work. Grant root-access for continue")
                .setDialogType(BaseDialogBuilder.Type.WARNING)
                .setAnimationDuration(300)
                .setAnimationInterpolator(new OvershootInterpolator())
                .setDismissOnTouchOutside(false)
                .build();
        readingDTSDialog = new BaseDialogBuilder(this)
                .setDialogTitle("Reading display specifications")
                .setDialogText("Please wait, program finish reading display specifications")
                .setDialogType(BaseDialogBuilder.Type.INDETERMINATE)
                .setAnimationDuration(300)
                .setAnimationInterpolator(new OvershootInterpolator())
                .setDismissOnTouchOutside(false)
                .build();
        modifyingDTSDialog = new BaseDialogBuilder(this)
                .setDialogTitle("Please wait, applying changes")
                .setDialogText("")
                .setDialogType(BaseDialogBuilder.Type.INDETERMINATE)
                .setAnimationDuration(300)
                .setAnimationInterpolator(new OvershootInterpolator())
                .setDismissOnTouchOutside(false)
                .build();
        modifyingDTSFinishDialog = new BaseDialogBuilder(this)
                .setDialogTitle("Modding DTBO finished")
                .setDialogText("Do you want to reboot for apply changes?")
                .setDialogType(BaseDialogBuilder.Type.INFO)
                .setAnimationDuration(300)
                .setAnimationInterpolator(new OvershootInterpolator())
                .setDismissOnTouchOutside(false)
                .build();

        noDTBOBlockDialog = new BaseDialogBuilder(this)
                .setDialogTitle("This device not supported")
                .setDialogText("This device doesn't have external DTBO block, so program can't work properly. Please wait while author add support for internal DTBO feature")
                .setDialogType(BaseDialogBuilder.Type.ERROR)
                .setAnimationDuration(300)
                .setAnimationInterpolator(new OvershootInterpolator())
                .setDismissOnTouchOutside(false)
                .build();
        noDTBOBlockDialog.setDialogDone("Exit", view -> {
            System.exit(0);
        });
        modifyingDTSFinishDialog.setDialogCancel("No", view -> modifyingDTSFinishDialog.closeDialog());
        modifyingDTSFinishDialog.setDialogDone("Reboot", view -> Shell.exec("reboot", true));
    }

    private void initializeDTS(){
        PM.put("intro_show", true);
        introDialog.closeDialog();
        readingDTSDialog.showDialog();
        new Thread(() -> {
            dtboBlock = new DTBOBlock();
            if(dtboBlock.isHaveBlock()) {
                display = new Display(MainActivity.this);
                int primaryColor = ContextCompat.getColor(MainActivity.this, R.color.primaryColor);
                DisplaySettings displaySettings = dtboBlock.getDisplaySettings();
                runOnUiThread(() -> {
                    displaySize.setText(new ColoredTextView.Builder()
                            .add("S", primaryColor, Span.Typeface.BOLD, 18).add("ize: ", Color.WHITE, 14).add(displaySettings.getPanelWidth() + "x" + displaySettings.getPanelHeight(), primaryColor, 14)
                            .build()
                    );
                    displayName.setText(new ColoredTextView.Builder()
                            .add("N", primaryColor, Span.Typeface.BOLD, 18).add("ame: ", Color.WHITE, 14).add(displaySettings.getPanelName(), primaryColor, 14)
                            .build()
                    );
                    displayFramerate.setText(new ColoredTextView.Builder()
                            .add("F", primaryColor, Span.Typeface.BOLD, 18).add("ramerate: ", Color.WHITE, 14).add(displaySettings.getPanelFramerate() + " Hz", primaryColor, 14)
                            .build()
                    );
                    displayHFrontPorch.setText(new ColoredTextView.Builder()
                            .add("H", primaryColor, Span.Typeface.BOLD, 18).add("orizontal front porch: ", Color.WHITE, 14).add(String.valueOf(displaySettings.gethFrontPorch()), primaryColor, 14)
                            .build()
                    );
                    displayHBackPorch.setText(new ColoredTextView.Builder()
                            .add("H", primaryColor, Span.Typeface.BOLD, 18).add("orizontal back porch: ", Color.WHITE, 14).add(String.valueOf(displaySettings.gethBackPorch()), primaryColor, 14)
                            .build()
                    );
                    displayHPulseWidth.setText(new ColoredTextView.Builder()
                            .add("H", primaryColor, Span.Typeface.BOLD, 18).add("orizontal pulse width: ", Color.WHITE, 14).add(String.valueOf(displaySettings.gethPulseWidth()), primaryColor, 14)
                            .build()
                    );

                    displayVFrontPorch.setText(new ColoredTextView.Builder()
                            .add("V", primaryColor, Span.Typeface.BOLD, 18).add("ertical front porch: ", Color.WHITE, 14).add(String.valueOf(displaySettings.getvFrontPorch()), primaryColor, 14)
                            .build()
                    );
                    displayVBackPorch.setText(new ColoredTextView.Builder()
                            .add("V", primaryColor, Span.Typeface.BOLD, 18).add("ertical back porch: ", Color.WHITE, 14).add(String.valueOf(displaySettings.getvBackPorch()), primaryColor, 14)
                            .build()
                    );
                    displayVPulseWidth.setText(new ColoredTextView.Builder()
                            .add("V", primaryColor, Span.Typeface.BOLD, 18).add("ertical pulse width: ", Color.WHITE, 14).add(String.valueOf(displaySettings.getvPulseWidth()), primaryColor, 14)
                            .build()
                    );
                    displaySyncSkew.setText(new ColoredTextView.Builder()
                            .add("S", primaryColor, Span.Typeface.BOLD, 18).add("ync skew: ", Color.WHITE, 14).add(String.valueOf(displaySettings.gethSyncSkew()), primaryColor, 14)
                            .build()
                    );
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
        initDialogs();
        introDialog.setDialogCancel("I don't have root-access", view -> System.exit(0));
        introDialog.setDialogDone("Grant", view -> {
            if (Shell.haveRoot()) {
                initializeDTS();
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
            array.put(0, "Safe");
            array.put(1, "Unsafe");
            array.put(3, "Danger");
            return array;
        });
        applyRefreshRateButton.setOnClickListener(view -> {
            modifyingDTSDialog.showDialog();
            log("Preparing DTBO block...");
            new Thread(() -> {
                dtboBlock.cleanModFolder();
                log("Extracting & decompiling DTBO...");
                dtboBlock.extract();
                log("Extracting & decompiling DTBO finished...");
                log("Reading display panel settings...");
                DisplaySettings displaySettings = dtboBlock.getDisplaySettings();
                log("Reading display panel settings finished...");
                displaySettings.setPanelFramerate(refreshSeekBar.getProgress());
                log("Writing display panel settings...");
                dtboBlock.writeDisplaySettings(displaySettings);
                log("Writing display panel settings finished...");
                log("Compressing & compiling DTBO...");
                dtboBlock.compile();
                log("Compressing & compiling DTBO finished...");
                log("Writing modded DTBO...");
                dtboBlock.write("/sdcard/dtbo_mod.img");
                log("Writing modded DTBO finished...");
                runOnUiThread(() -> {
                    modifyingDTSDialog.closeDialog();
                    modifyingDTSFinishDialog.showDialog();
                });
            }).start();
        });
        rebootButton.setOnClickListener(view -> Shell.exec("reboot", true));
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