package com.gat.common.adapter.impl;

import android.support.annotation.LayoutRes;
import android.view.ViewGroup;
import android.widget.Button;
import com.gat.R;
import com.gat.common.adapter.ItemViewHolder;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mryit on 5/13/2017.
 */

public class LoadMoreViewHolder extends ItemViewHolder<LoadMoreItem> {

    @BindView(R.id.button_load_more)
    Button buttonLoadMore;

    private OnItemLoadMoreClickListener listener;

    public LoadMoreViewHolder(ViewGroup parent, @LayoutRes int layoutId, OnItemLoadMoreClickListener callback) {
        super(parent, layoutId);
        ButterKnife.bind(this, itemView);
        listener = callback;
    }

    @Override
    public void onBindItem(LoadMoreItem item) {
        super.onBindItem(item);

        buttonLoadMore.setOnClickListener(view -> {
            if (listener != null)
                listener.onLoadMoreClick();
        });

    }
}
