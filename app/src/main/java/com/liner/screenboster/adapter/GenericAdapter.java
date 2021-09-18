package com.liner.screenboster.adapter;

import android.os.Handler;
import android.util.SparseArray;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@SuppressWarnings("rawtypes | UnusedReturnValue | unused | ConstantConditions | unchecked")
public class GenericAdapter extends RecyclerView.Adapter<GenericAdapter.ViewHolder> {
    private final List<Object> items = new ArrayList<>();
    private final SparseArray<Class> binders = new SparseArray<>();
    private final SparseArray<Class> layoutsToTypes = new SparseArray<>();
    private final Map<Class, Integer> typesToLayouts = new HashMap<>();
    private final Map<Class, OnInteract> onInteractHashMap = new HashMap<>();
    private final Map<Class, OnBindedCallback> boundCallbackMap = new HashMap<>();


    public <R extends Binder<T>, T> GenericAdapter register(@LayoutRes int layout, @NonNull Class<T> clazz, @NonNull Class<R> binder) {
        binders.put(layout, binder);
        typesToLayouts.put(clazz, layout);
        layoutsToTypes.put(layout, clazz);
        return this;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Class binderClass = binders.get(viewType);
        if (binderClass != null) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
            try {
                Binder binder = (Binder) binderClass.newInstance();
                binder.setItemView(itemView);
                return new ViewHolder(itemView, binder, layoutsToTypes.get(viewType));
            } catch (IllegalAccessException | InstantiationException e) {
                e.printStackTrace();
            }
        }
        throw new IllegalStateException("No binder added for viewType " + viewType);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public int indexOf(@NonNull Object o) {
        return items.indexOf(o);
    }

    public void remove(int index) {
        items.remove(index);
    }

    public void insert(int index, @NonNull Object o) {
        items.add(index, o);
    }


    @Override
    public int getItemViewType(int position) {
        Object o = items.get(position);
        if (o == null)
            throw new IllegalStateException("Item at index " + position + " is null!");
        if (typesToLayouts.containsKey(o.getClass())) {
            return typesToLayouts.get(o.getClass());
        } else {
            throw new IllegalStateException("Class " + o.getClass().getSimpleName() + " not registered in the adapter");
        }
    }

    public Class getObjectClassType(int position) {
        return layoutsToTypes.get(position);
    }


    public void add(@NonNull Object item) {
        items.add(item);
    }

    public void add(@NonNull Object... items) {
        Collections.addAll(this.items, items);
    }


    public void add(@NonNull Collection<?> items) {
        this.items.addAll(items);
    }

    public void clear() {
        items.clear();
    }


    public void set(@NonNull Object... items) {
        this.items.clear();
        Collections.addAll(this.items, items);
    }


    public void set(@NonNull Collection<?> items) {
        this.items.clear();
        this.items.addAll(items);
    }

    public <R extends Binder<T>, T> void onInteract(@NonNull Class<T> clazz, @NonNull OnInteract<R, T> onInteract) {
        onInteractHashMap.put(clazz, onInteract);
    }


    public <S extends Binder<T>, T> void onBinded(@NonNull Class<T> clazz, @NonNull OnBindedCallback<T, S> callback) {
        boundCallbackMap.put(clazz, callback);
    }

    @NonNull
    public <T> T get(int i) {
        return (T) items.get(i);
    }

    public List getItems() {
        return items;
    }

    class ViewHolder<R extends Binder<T>, T> extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener, View.OnDragListener {
        private final R binder;
        @NonNull
        private final Class<T> modelClass;
        private T model = null;

        ViewHolder(@NonNull View itemView, R binder, @NonNull Class<T> modelClass) {
            super(itemView);
            this.modelClass = modelClass;
            this.binder = binder;
            this.binder.init();
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            itemView.setOnDragListener(this);
        }

        void bind(T model) {
            this.model = model;
            binder.bind(model);
            if (boundCallbackMap.containsKey(modelClass)) {
                Objects.requireNonNull(boundCallbackMap.get(modelClass)).itemBound(binder, model);
            }
        }

        @Override
        public void onClick(@NonNull View view) {
            if (onInteractHashMap.containsKey(modelClass)) {
                Objects.requireNonNull(onInteractHashMap.get(modelClass)).onClick(binder, model);
            }
        }

        @Override
        public boolean onLongClick(View v) {
            if (onInteractHashMap.containsKey(modelClass)) {
                Objects.requireNonNull(onInteractHashMap.get(modelClass)).onLongClick(binder, model);
            }
            return true;
        }

        @Override
        public boolean onDrag(View v, DragEvent event) {
            if (onInteractHashMap.containsKey(modelClass)) {
                Objects.requireNonNull(onInteractHashMap.get(modelClass)).onDrag(binder, event, model);
            }
            return false;
        }

        public boolean allowDrag() {
            return binder.getDragDirections() != 0;
        }

        public int getDragDirections() {
            return binder.getDragDirections();
        }

        public boolean allowSwipe() {
            return binder.getSwipeDirections() != 0;
        }

        public int getSwipeDirections() {
            return binder.getSwipeDirections();
        }
    }

}
