package com.liner.screenboster;

import android.annotation.SuppressLint;
import android.util.Log;

import com.liner.screenboster.utils.Files;
import com.liner.screenboster.utils.Shell;
import com.liner.screenboster.utils.TextUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class DTBOBlock {
    private static final String TAG = DTBOBlock.class.getSimpleName();
    private final String blockAddress;
    private final boolean haveBlock;

    public DTBOBlock() {
        Log.d(TAG, "Creating instance of DTBO");
        Shell.Result result = Shell.exec("cd /dev/block/by-name;ls -l dtbo", true);
        if (result.isSuccess()) {
            haveBlock = true;
            blockAddress = result.getResponse().substring(result.getResponse().indexOf("/"));
            Log.d(TAG, "Device have DTBO block at: " + blockAddress);
            read(Core.DIRECTORY_STOCK_DTBO + "dtbo.img");
            read("/sdcard/dtbo_backup.img");
            Log.d(TAG, "Copying image to " + Core.DIRECTORY_MOD_DTBO);
            Shell.exec("cp " + Core.DIRECTORY_STOCK_DTBO + "/dtbo.img " + Core.DIRECTORY_MOD_DTBO, true);
        } else {
            //TODO Implement kernel extraction variant
            Log.d(TAG, "Device doesn't have DTBO block! Extracting from kernel not implemented now!");
            haveBlock = false;
            blockAddress = "";
        }
    }

    public boolean read(String path) {
        Log.d(TAG, "Reading " + blockAddress + " to " + path);
        return Shell.exec("dd if=" + blockAddress + " of=" + path, true).isSuccess();
    }

    public boolean write(String path) {
        Log.d(TAG, "Writing " + blockAddress + " to " + path);
        return Shell.exec("dd if=" + path + " of=" + blockAddress, true).isSuccess();
    }

    @SuppressLint("SimpleDateFormat")
    public void makeBackup() {
        if (haveBlock) {
            read("sdcard/BACKUP_dtbo_" + new SimpleDateFormat("dd-MM-yyyy").format(new Date(System.currentTimeMillis())) + ".img");
        }
    }

    public boolean cleanModFolder() {
        Log.d(TAG, "Cleaning " + Core.DIRECTORY_MOD_DTB);
        Shell.exec(true, "rm -rf " + Core.DIRECTORY_MOD_DTB + "*");
        Log.d(TAG, "Cleaning " + Core.DIRECTORY_MOD_DTS);
        Shell.exec(true, "rm -rf " + Core.DIRECTORY_MOD_DTS + "*");
        Shell.exec(true, "rm -rf " + Core.DIRECTORY_MOD_DTBO + "dtbo_mod.img");
        return true;
    }

    public DisplaySettings getDisplaySettings() {
        Log.d(TAG, "Start reading display settings");
        AtomicReference<DisplaySettings> displaySettings = new AtomicReference<>();
        File[] dtsFiles = new File(Core.DIRECTORY_MOD_DTS).listFiles();
        if (dtsFiles == null || dtsFiles.length == 0) {
            Log.d(TAG, "DTS files not found, extracting DTBO");
            extract();
            return getDisplaySettings();
        } else {
            Arrays.stream(dtsFiles).forEach(dts -> {
                String dtsContent = Files.readFile(dts);
                if (dtsContent != null) {
                    int panelWidth = Integer.parseInt(TextUtils.getRegex(dtsContent, "qcom,mdss-dsi-panel-width = <([0-9a-fA-FxX]*)>;", 1).replace("0x", ""), 16);
                    int panelHeight = Integer.parseInt(TextUtils.getRegex(dtsContent, "qcom,mdss-dsi-panel-height = <([0-9a-fA-FxX]*)>;", 1).replace("0x", ""), 16);
                    if (panelWidth != 0 && panelHeight != 0) {
                        displaySettings.set(new DisplaySettings(
                                dts,
                                dtsContent
                        ));
                    }
                }
            });
        }
        Log.d(TAG, "Display settings read finished");
        return displaySettings.get();
    }

    public void writeDisplaySettings(DisplaySettings displaySettings) {
        int panelFrameRate = displaySettings.getPanelFramerate();
        Log.d(TAG, "Starting patching DTS files");
        File[] dtsFiles = new File(Core.DIRECTORY_MOD_DTS).listFiles();
        if (dtsFiles == null || dtsFiles.length == 0) {
            Log.d(TAG, "DTS files not found, extracting DTBO");
            extract();
        }
        Arrays.stream(Objects.requireNonNull(dtsFiles)).forEach(dts -> {
            String dtsContent = Files.readFile(dts);
            if (dtsContent != null) {
                String frameRateHex = "0x" + Integer.toHexString(panelFrameRate);
                Log.d(TAG, "New framerate value: "+frameRateHex);
                String panelClockRatePatch = TextUtils.getRegex(dtsContent, "qcom,mdss-dsi-panel-clockrate = <([0-9a-fA-FxX]*)>;", 1);
                if (!panelClockRatePatch.equals("0")) {
                    Log.d(TAG, "Determinating dsi-panel-clockrate...");
                    long clockRate = Long.parseLong(panelClockRatePatch.replace("0x", ""), 16);
                    String clockRatePatch = "";
                    if (((clockRate * 60) / 830000000L) == panelFrameRate) {
                        clockRatePatch = "0x" + Long.toHexString(panelFrameRate * 830000000L / 60);
                    } else if (((clockRate * 60) / 400000000L) == panelFrameRate) {
                        clockRatePatch = "0x" + Long.toHexString(panelFrameRate * 400000000L / 60);
                    } else if (((clockRate * 60) / 900000000L) == panelFrameRate) {
                        clockRatePatch = "0x" + Long.toHexString(panelFrameRate * 900000000L / 60);
                    } else if (((clockRate * 60) / 1106000000L) == panelFrameRate) {
                        clockRatePatch = "0x" + Long.toHexString(panelFrameRate * 1106000000L / 60);
                    } else if (((clockRate * 60) / 850000000L) == panelFrameRate) {
                        clockRatePatch = "0x" + Long.toHexString(panelFrameRate * 850000000L / 60);
                    } else if (((clockRate * 60) / 1198162980L) == panelFrameRate) {
                        clockRatePatch = "0x" + Long.toHexString(panelFrameRate * 1198162980L / 60);
                    }
                    Log.d(TAG, "Patching panel-clockrate to "+clockRatePatch);
                    dtsContent = TextUtils.replace(dtsContent, "qcom,mdss-dsi-panel-clockrate = <([0-9a-fA-FxX]*)>;", 1, clockRatePatch);
                }
                Log.d(TAG, "Patching panel-framerate to "+frameRateHex);
                dtsContent = TextUtils.replace(dtsContent, "qcom,mdss-dsi-panel-framerate = <([0-9a-fA-FxX]*)>;", 1, frameRateHex);
                Log.d(TAG, "Patching supported-dfps-list to "+frameRateHex+" "+frameRateHex+" "+frameRateHex);
                dtsContent = TextUtils.replace(dtsContent, "qcom,dsi-supported-dfps-list = <([0-9a-fA-FxX ]*)>;", 1, frameRateHex+" "+frameRateHex+" "+frameRateHex);
                Log.d(TAG, "Patching max-refresh-rate to "+frameRateHex);
                dtsContent = TextUtils.replace(dtsContent, "qcom,mdss-dsi-max-refresh-rate = <([0-9a-fA-FxX]*)>", 1, frameRateHex);
                Log.d(TAG, "Saving patched DTS file");
                Files.writeFile(dts, dtsContent);
            }
        });
        Log.d(TAG, "Patching DTS finished");
    }

    public boolean compile() {
        long startTime = System.currentTimeMillis();
        Log.d(TAG, "Starting compiling DTBO");
        File[] dtsFiles = new File(Core.DIRECTORY_MOD_DTS).listFiles();
        if (dtsFiles == null)
            return false;
        if (dtsFiles.length == 0)
            return false;
        Log.d(TAG, "Clearing old DTB files from " + Core.DIRECTORY_MOD_DTB);
        Shell.exec(true, "rm -rf " + Core.DIRECTORY_MOD_DTB + "*");
        Arrays.stream(dtsFiles).forEach(dts -> {
            Log.d(TAG, "Compiling " + dts.getName() + " to " + Core.DIRECTORY_MOD_DTB);
            Shell.exec(true,
                    "cd " + Core.DIRECTORY_ROOT,
                    Core.DIRECTORY_TOOLS + "dtc -I dts -O dtb -f " + dts.getAbsolutePath() + " -o " + Core.DIRECTORY_MOD_DTB + dts.getName().replace(".dts", ".dtb")
            );
        });
        Log.d(TAG, "Compressing DTB files from " + Core.DIRECTORY_MOD_DTB + " to " + Core.DIRECTORY_MOD_DTBO + "dtbo_mod.img");
        Shell.Result result = Shell.exec(true,
                "cd " + Core.DIRECTORY_ROOT,
                Core.DIRECTORY_TOOLS + "mkdtimg create " + Core.DIRECTORY_MOD_DTBO + "dtbo_mod.img " + Core.DIRECTORY_MOD_DTB + "*.dtb"
        );
        if (result.isSuccess()) {
            Shell.exec(true, "cp -r " + Core.DIRECTORY_MOD_DTBO + "dtbo_mod.img /sdcard/");
            Log.d(TAG, "Compiling DTBO finished, took " + (System.currentTimeMillis() - startTime) + " ms");
            return true;
        } else {
            Log.d(TAG, "Extraction DTBO failed, " + result.getResponse());
            return false;
        }
    }

    public boolean extract() {
        long startTime = System.currentTimeMillis();
        Log.d(TAG, "Starting extraction DTBO block");
        Shell.Result result = Shell.exec(true,
                "cd " + Core.DIRECTORY_ROOT,
                Core.DIRECTORY_TOOLS + "imgtool.android.arm64 " + Core.DIRECTORY_MOD_DTBO + "dtbo.img extract"
        );
        if (result.isSuccess()) {
            Log.d(TAG, "Extraction DTBO finished successfully!");
            Log.d(TAG, "Copying DTB files to " + Core.DIRECTORY_MOD_DTB);
            Shell.exec(true,
                    "cp -r " + Core.DIRECTORY_ROOT + "extracted/* " + Core.DIRECTORY_MOD_DTB,
                    "rm -rf " + Core.DIRECTORY_ROOT + "extracted"
            );
            File[] dtbFiles = new File(Core.DIRECTORY_MOD_DTB).listFiles();
            if (dtbFiles == null)
                return false;
            Arrays.stream(dtbFiles).forEach(dtb -> {
                if (dtb.isFile()) {
                    Log.d(TAG, "Decompiling " + dtb.getName() + " to " + Core.DIRECTORY_MOD_DTS);
                    Shell.exec(true,
                            "cd " + Core.DIRECTORY_ROOT,
                            Core.DIRECTORY_TOOLS + "dtc -I dtb -O dts -f " + dtb.getAbsolutePath() + " -o " + Core.DIRECTORY_MOD_DTS + dtb.getName().replace(".dtb", ".dts")
                    );
                }
            });
            Log.d(TAG, "Extraction DTBO finished, took " + (System.currentTimeMillis() - startTime) + " ms");
        } else
            Log.d(TAG, "Extraction DTBO failed, " + result.getResponse());
        return false;
    }

    @Override
    public String toString() {
        return "DTBOBlock{" +
                "blockAddress='" + blockAddress + '\'' +
                ", haveBlock=" + haveBlock +
                '}';
    }
}
