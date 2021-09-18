package com.liner.screenboster;

import com.liner.screenboster.utils.TextUtils;

import java.io.File;

public class DTSDisplaySettings {
    private final File dtsFile;
    private String panelName;
    private int panelWidth;
    private int panelHeight;
    private int hFrontPorch;
    private int hBackPorch;
    private int hPulseWidth;
    private int hSyncSkew;
    private int vFrontPorch;
    private int vBackPorch;
    private int vPulseWidth;
    private int panelFramerate;

    public DTSDisplaySettings(File dtsFile, String dtsContent){
        this.dtsFile = dtsFile;
        this.panelName = TextUtils.getRegex(dtsContent, "qcom,mdss-dsi-panel-name = \"([a-zA-Z0-9 ]*)\"", 1);
        this.panelWidth = Integer.parseInt(TextUtils.getRegex(dtsContent, "qcom,mdss-dsi-panel-width = <([0-9a-fA-FxX]*)>;", 1).replace("0x", ""), 16);
        this.panelHeight = Integer.parseInt(TextUtils.getRegex(dtsContent, "qcom,mdss-dsi-panel-height = <([0-9a-fA-FxX]*)>;", 1).replace("0x", ""), 16);
        this.hFrontPorch = Integer.parseInt(TextUtils.getRegex(dtsContent, "qcom,mdss-dsi-h-front-porch = <([0-9a-fA-FxX]*)>;", 1).replace("0x", ""), 16);
        this.hBackPorch = Integer.parseInt(TextUtils.getRegex(dtsContent, "qcom,mdss-dsi-h-back-porch = <([0-9a-fA-FxX]*)>;", 1).replace("0x", ""), 16);
        this.hPulseWidth = Integer.parseInt(TextUtils.getRegex(dtsContent, "qcom,mdss-dsi-h-pulse-width = <([0-9a-fA-FxX]*)>;", 1).replace("0x", ""), 16);
        this.hSyncSkew = Integer.parseInt(TextUtils.getRegex(dtsContent, "qcom,mdss-dsi-h-sync-skew = <([0-9a-fA-FxX]*)>;", 1).replace("0x", ""), 16);
        this.vFrontPorch = Integer.parseInt(TextUtils.getRegex(dtsContent, "qcom,mdss-dsi-v-front-porch = <([0-9a-fA-FxX]*)>;", 1).replace("0x", ""), 16);
        this.vBackPorch = Integer.parseInt(TextUtils.getRegex(dtsContent, "qcom,mdss-dsi-v-back-porch = <([0-9a-fA-FxX]*)>;", 1).replace("0x", ""), 16);
        this.vPulseWidth = Integer.parseInt(TextUtils.getRegex(dtsContent, "qcom,mdss-dsi-v-pulse-width = <([0-9a-fA-FxX]*)>;", 1).replace("0x", ""), 16);
        this.panelFramerate = Integer.parseInt(TextUtils.getRegex(dtsContent, "qcom,mdss-dsi-panel-framerate = <([0-9a-fA-FxX]*)>;", 1).replace("0x", ""), 16);
    }

    public String getPanelName() {
        return panelName;
    }

    public File getDtsFile() {
        return dtsFile;
    }

    public void setPanelName(String panelName) {
        this.panelName = panelName;
    }

    public int getPanelWidth() {
        return panelWidth;
    }

    public void setPanelWidth(int panelWidth) {
        this.panelWidth = panelWidth;
    }

    public int getPanelHeight() {
        return panelHeight;
    }

    public void setPanelHeight(int panelHeight) {
        this.panelHeight = panelHeight;
    }

    public int gethFrontPorch() {
        return hFrontPorch;
    }

    public void sethFrontPorch(int hFrontPorch) {
        this.hFrontPorch = hFrontPorch;
    }

    public int gethBackPorch() {
        return hBackPorch;
    }

    public void sethBackPorch(int hBackPorch) {
        this.hBackPorch = hBackPorch;
    }

    public int gethPulseWidth() {
        return hPulseWidth;
    }

    public void sethPulseWidth(int hPulseWidth) {
        this.hPulseWidth = hPulseWidth;
    }

    public int gethSyncSkew() {
        return hSyncSkew;
    }

    public void sethSyncSkew(int hSyncSkew) {
        this.hSyncSkew = hSyncSkew;
    }

    public int getvFrontPorch() {
        return vFrontPorch;
    }

    public void setvFrontPorch(int vFrontPorch) {
        this.vFrontPorch = vFrontPorch;
    }

    public int getvBackPorch() {
        return vBackPorch;
    }

    public void setvBackPorch(int vBackPorch) {
        this.vBackPorch = vBackPorch;
    }

    public int getvPulseWidth() {
        return vPulseWidth;
    }

    public void setvPulseWidth(int vPulseWidth) {
        this.vPulseWidth = vPulseWidth;
    }

    public int getPanelFramerate() {
        return panelFramerate;
    }

    public void setPanelFramerate(int panelFramerate) {
        this.panelFramerate = panelFramerate;
    }

    @Override
    public String toString() {
        return "DTSDisplaySettings{" +
                "dtsFile=" + dtsFile +
                ", panelName='" + panelName + '\'' +
                ", panelWidth=" + panelWidth +
                ", panelHeight=" + panelHeight +
                ", hFrontPorch=" + hFrontPorch +
                ", hBackPorch=" + hBackPorch +
                ", hPulseWidth=" + hPulseWidth +
                ", hSyncSkew=" + hSyncSkew +
                ", vFrontPorch=" + vFrontPorch +
                ", vBackPorch=" + vBackPorch +
                ", vPulseWidth=" + vPulseWidth +
                ", panelFramerate=" + panelFramerate +
                '}';
    }
}
