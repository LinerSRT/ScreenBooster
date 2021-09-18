

package com.liner.screenboster.utils.bottomsheetcore;

import android.app.Activity;

import androidx.annotation.NonNull;



public abstract class BaseBottomSheet extends BottomSheetContainer {

    public BaseBottomSheet(@NonNull Activity hostActivity, @NonNull BaseConfig config) {
        super(hostActivity, config);
    }
}