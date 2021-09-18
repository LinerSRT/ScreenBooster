package com.liner.screenboster.adapter;


import android.view.DragEvent;

import androidx.annotation.NonNull;

@SuppressWarnings("UnusedReturnValue | unused")
public interface OnInteract<R extends Binder<T>, T> {
    void onClick(@NonNull R binder, @NonNull T model);
    void onLongClick(@NonNull R binder, @NonNull T model);
    boolean onDrag(@NonNull R binder, DragEvent event, @NonNull T model);
}
