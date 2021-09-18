package com.liner.screenboster;

import android.content.Context;
import android.util.Log;

import com.liner.screenboster.utils.Assets;
import com.liner.screenboster.utils.Files;
import com.liner.screenboster.utils.ShellUtils;
import com.liner.screenboster.utils.TextUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class DTBO {
    private final String block;

    public DTBO() {
        ShellUtils.CommandResult command = ShellUtils.execCommand(
                "cd /dev/block/by-name;ls -l dtbo",
                true,
                true
        );
        block = command.result == 0 ? command.successMsg.substring(command.successMsg.indexOf("/")) : null;
    }

    public boolean save(String path) {
        ShellUtils.CommandResult command = ShellUtils.execCommand(
                "dd if=" + block + " of=" + path,
                true,
                true
        );
        return command.result == 0;
    }

    public boolean write(String path) {
        ShellUtils.CommandResult command = ShellUtils.execCommand(
                "dd if=" + path + " of=" + block,
                true,
                true
        );
        return command.result == 0;
    }

    public void backup(Operation operation) {
        operation.onStart();
        new Thread(() -> {
            if (save("sdcard/dtbo_backup.img")) {
                operation.onFinished();
            } else {
                operation.onFailed();
            }
        }).start();
    }

    public void restore(Operation operation) {
        operation.onStart();
        new Thread(() -> {
            if (write("sdcard/dtbo_backup.img")) {
                operation.onFinished();
            } else {
                operation.onFailed();
            }
        }).start();
    }

    public void writeDisplayFrameRate(Context context, int panelFrameRate, Operation operation) {
        readDTS(context, new DTSReadOperation() {
            @Override
            public void onStart() {
                operation.onStart();
            }

            @Override
            public void onRead(String dtsContent, File dtsFile) {
                String panelClockRatePatch = TextUtils.getRegex(dtsContent, "qcom,mdss-dsi-panel-clockrate = <([0-9a-fA-FxX]*)>;", 1);
                if (!panelClockRatePatch.equals("0")) {
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
                    dtsContent = TextUtils.replace("qcom,mdss-dsi-panel-clockrate = <([0-9a-fA-FxX]*)>;", 1, dtsContent, clockRatePatch);
                }
                dtsContent =  TextUtils.replace("qcom,mdss-dsi-panel-framerate = <([0-9a-fA-FxX]*)>;", 1, dtsContent,  "0x" + Integer.toHexString(panelFrameRate));
                dtsContent =  TextUtils.replace("qcom,dsi-supported-dfps-list = <([0-9a-fA-FxX]*) ([0-9a-fA-FxX]*) ([0-9a-fA-FxX]*)>", 1, dtsContent,  "0x" + Integer.toHexString(panelFrameRate));
                dtsContent =  TextUtils.replace("qcom,mdss-dsi-max-refresh-rate = <([0-9a-fA-FxX]*)>", 1, dtsContent,  "0x" + Integer.toHexString(panelFrameRate));
                Files.writeFile(dtsFile, dtsContent);
            }

            @Override
            public void onFinished() {
                operation.onFinished();
            }

            @Override
            public void onFailed() {
                operation.onFailed();
            }
        });
    }

    public void readDisplaySettings(Context context, DTSReadDisplayOperation operation) {
        List<DTSDisplaySettings> displaySettingsList = new ArrayList<>();
        readDTS(context, new DTSReadOperation() {
            @Override
            public void onStart() {
                operation.onStart();
            }

            @Override
            public void onRead(String dtsContent, File dtsFile) {
                int panelWidth = Integer.parseInt(TextUtils.getRegex(dtsContent, "qcom,mdss-dsi-panel-width = <([0-9a-fA-FxX]*)>;", 1).replace("0x", ""), 16);
                int panelHeight = Integer.parseInt(TextUtils.getRegex(dtsContent, "qcom,mdss-dsi-panel-height = <([0-9a-fA-FxX]*)>;", 1).replace("0x", ""), 16);
                if (panelWidth != 0 && panelHeight != 0)
                    displaySettingsList.add(new DTSDisplaySettings(
                            dtsFile,
                            dtsContent
                    ));
            }

            @Override
            public void onFinished() {
                operation.onFinished(displaySettingsList);
            }

            @Override
            public void onFailed() {
                operation.onFailed();
            }
        });
    }

    public void readDTS(Context context, DTSReadOperation operation) {
        operation.onStart();
        new Thread(() -> {
            File[] dtsFiles = new File(context.getFilesDir(), "extracted/dts").listFiles();
            if (dtsFiles != null) {
                for (File file : dtsFiles) {
                    String content = Files.readFile(file);
                    if (content != null) {
                        operation.onRead(content, file);
                    }
                }
                operation.onFinished();
            } else {
                operation.onFailed();
            }
        }).start();
    }

    public void extract(Context context, Operation operation) {
        operation.onStart();
        new Thread(() -> {
            ShellUtils.execCommand("rm -rf " + context.getFilesDir() + "/*", true);
            ShellUtils.execCommand("mkdir -p " + context.getFilesDir() + "/extracted/dts/", true);
            Assets.copy(context, "imgtool", "imgtool.android.arm64");
            Assets.copy(context, "dtc", "dtc");
            Assets.copy(context, "mkdtimg", "mkdtimg");
            save(context.getFilesDir() + "/dtbo.img");
            ShellUtils.execCommand("chmod 777 -R " + context.getFilesDir(), true);
            ShellUtils.CommandResult commandResult = ShellUtils.execCommand("cd " + context.getFilesDir() + ";./imgtool.android.arm64 dtbo.img extract", true, true);
            if (commandResult.isSuccess()) {
                File[] dtbFiles = new File(context.getFilesDir(), "extracted").listFiles();
                if (dtbFiles != null) {
                    for (File file : dtbFiles) {
                        if (file.isFile()) {
                            String command = "cd " + context.getFilesDir() + ";./dtc -I dtb -O dts -f ./extracted/" + file.getName() + " -o ./extracted/dts/" + file.getName().replace(".dtb", ".dts");
                            commandResult = ShellUtils.execCommand(command, true, true);
                            if (!commandResult.isSuccess()) {
                                operation.onFailed();
                                return;
                            }
                        }
                    }
                    operation.onFinished();
                } else
                    operation.onFailed();
            } else
                operation.onFailed();
        }).start();
    }

    public void compress(Context context, Operation operation) {
        operation.onStart();
        new Thread(() -> {
            ShellUtils.execCommand("mkdir -p " + context.getFilesDir() + "/extracted/dtb_mod/", true);
            ShellUtils.execCommand("chmod 777 -R " + context.getFilesDir(), true);
            File[] dtsFiles = new File(context.getFilesDir(), "extracted/dts").listFiles();
            ShellUtils.CommandResult commandResult;
            if (dtsFiles != null) {
                for (File file : dtsFiles) {
                    commandResult = ShellUtils.execCommand("cd " + context.getFilesDir() + ";./dtc -I dts -O dtb -f ./extracted/dts/" + file.getName() + " -o ./extracted/dtb_mod/" + file.getName().replace(".dts", ".dtb"), true, true);
                    if (!commandResult.isSuccess()) {
                        operation.onFailed();
                        return;
                    }
                }
                commandResult = ShellUtils.execCommand("cd " + context.getFilesDir() + ";./mkdtimg create ./extracted/dtb_mod/dtbo_modified.img ./extracted/dtb_mod/*.dtb", true, true);
                if (commandResult.isSuccess()) {
                    ShellUtils.execCommand("cp " + context.getFilesDir() + "/extracted/dtb_mod/dtbo_modified.img sdcard/dtbo_modded.img", true, true);
                    operation.onFinished();
                } else {
                    operation.onFailed();
                }
            } else {
                operation.onFailed();
            }
        }).start();
    }

    public String getBlock() {
        return block;
    }

    public boolean isCorrect() {
        return block != null;
    }

    public interface Operation {
        void onStart();

        void onFinished();

        void onFailed();
    }

    public interface DTSReadOperation {
        void onStart();

        void onRead(String dtsContent, File dtsFile);

        void onFinished();

        void onFailed();
    }

    public interface DTSReadDisplayOperation {
        void onStart();

        void onFinished(List<DTSDisplaySettings> displaySettingsList);

        void onFailed();
    }

    @Override
    public String toString() {
        return "DTBO{" +
                "block='" + block + '\'' +
                '}';
    }
}
