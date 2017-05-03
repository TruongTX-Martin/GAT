package com.gat.feature.message.adapter;

import android.support.annotation.IntDef;
import android.view.ViewGroup;

import com.gat.R;
import com.gat.common.adapter.Item;
import com.gat.common.adapter.ItemAdapter;
import com.gat.common.adapter.ItemViewHolder;
import com.gat.feature.message.item.GroupItem;
import com.gat.feature.message.item.LoadingMessage;
import com.gat.feature.message.viewholder.GroupMessageViewHolder;
import com.gat.feature.message.viewholder.LoadingMessageViewHolder;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by ducbtsn on 4/1/17.
 */

public class GroupMessageAdapter extends ItemAdapter {
    @IntDef({GroupMessageAdapter.Type.LOADING, GroupMessageAdapter.Type.MESSAGE})
    @Retention(RetentionPolicy.SOURCE)
    @interface Type {
        int LOADING = 1;
        int MESSAGE    = 2;
    }
    public GroupMessageAdapter() {
        setReady();
    }
    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case GroupMessageAdapter.Type.MESSAGE:
                return new GroupMessageViewHolder(parent, R.layout.group_message_item);
            case GroupMessageAdapter.Type.LOADING:
                return new LoadingMessageViewHolder(parent, R.layout.search_item_loading);
        }
        throw new IllegalArgumentException("Not support type " + viewType);
    }

    @Override
    public @GroupMessageAdapter.Type
    int getItemViewType(int position) {
        Item item = getItemAt(position);
        if(item instanceof LoadingMessage)
            return GroupMessageAdapter.Type.LOADING;
        if(item instanceof GroupItem)
            return GroupMessageAdapter.Type.MESSAGE;
        throw new IllegalArgumentException("Not support item " + item);
    }

    public boolean hasLoadMoreItem() {
        if(items.isEmpty())
            return false;
        Item item = items.get(items.size() - 1);
        return item instanceof LoadingMessage && !((LoadingMessage)item).fullHeight();
    }

    public void completeLoading() {
        if (!items.isEmpty()) {
            int size = items.size() - 1;
            Item item = items.get(size);
            if (item instanceof  LoadingMessage && !((LoadingMessage)item).fullHeight()) {
                items.remove(size);
            }
        }
    }
}
