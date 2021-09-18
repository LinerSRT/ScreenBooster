package com.liner.screenboster.views;

import android.view.View;

public class BaseDialogSelectionItem {
    private int iconResource;
    private String itemName;
    private View.OnClickListener clickListener;

    public BaseDialogSelectionItem(int iconResource, String itemName, View.OnClickListener clickListener) {
        this.iconResource = iconResource;
        this.itemName = itemName;
        this.clickListener = clickListener;
    }

    public int getIconResource() {
        return iconResource;
    }

    public void setIconResource(int iconResource) {
        this.iconResource = iconResource;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public View.OnClickListener getClickListener() {
        return clickListener;
    }

    public void setClickListener(View.OnClickListener clickListener) {
        this.clickListener = clickListener;
    }
}
