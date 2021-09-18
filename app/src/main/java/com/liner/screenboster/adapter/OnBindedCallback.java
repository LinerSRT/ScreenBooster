package com.liner.screenboster.adapter;


import androidx.annotation.NonNull;

public interface OnBindedCallback<T, S extends Binder<T>> {
    void itemBound(@NonNull S binder, @NonNull T model);
}
