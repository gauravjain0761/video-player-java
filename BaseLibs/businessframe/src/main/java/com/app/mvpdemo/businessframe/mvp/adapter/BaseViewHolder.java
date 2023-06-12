package com.app.mvpdemo.businessframe.mvp.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;

/**
 * Base class viewHolder inherited from recyclerview viewHolder
 *
 * @author weiwen
 * @created 2022/2/11 3:23 PM
 */
public abstract class BaseViewHolder<T> extends RecyclerView.ViewHolder {
    public BaseViewHolder(@NonNull ViewBinding itemView) {
        super(itemView.getRoot());
    }

    /**
     * 更新holder  view内容
     *
     * @param entity
     */
    public void updateHolder(T entity) {

    }

    public void updateHolder(T entity, int position) {

    }
}