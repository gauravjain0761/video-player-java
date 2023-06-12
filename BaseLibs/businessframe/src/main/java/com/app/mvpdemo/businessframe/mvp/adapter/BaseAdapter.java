package com.app.mvpdemo.businessframe.mvp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.viewbinding.ViewBinding;

/**
 * adapter base class
 */
public abstract class BaseAdapter<T> extends ListAdapter<T, BaseViewHolder<T>> {

    protected BaseAdapter(@NonNull DiffUtil.ItemCallback<T> diffCallback) {
        super(diffCallback);
    }


    @NonNull
    @Override
    public BaseViewHolder<T> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewBinding view = createView(parent, viewType);
        return createViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        holder.updateHolder(getItem(position));
        holder.updateHolder(getItem(position), position);
    }

    /**
     * create view
     *
     * @param parent
     * @param viewType
     * @return
     */
    protected abstract ViewBinding createView(ViewGroup parent, int viewType);

    /**
     * create view holder with view type
     *
     * @param view
     * @param viewType
     * @return
     */
    protected abstract BaseViewHolder<T> createViewHolder(ViewBinding view, int viewType);
}
