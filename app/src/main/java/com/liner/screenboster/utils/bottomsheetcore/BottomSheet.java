

package com.liner.screenboster.utils.bottomsheetcore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


public interface BottomSheet {

    
    void show();

    
    void show(boolean animate);

    
    void dismiss();

    
    void dismiss(boolean animate);

    
    void setOnDismissListener(@Nullable OnDismissListener onDismissListener);

    
    @NonNull
    State getState();


    
    enum State {

        COLLAPSED,
        COLLAPSING,
        EXPANDED,
        EXPANDING

    }


    
    interface OnDismissListener {

        
        void onDismiss(@NonNull BottomSheet bottomSheet);

    }


}