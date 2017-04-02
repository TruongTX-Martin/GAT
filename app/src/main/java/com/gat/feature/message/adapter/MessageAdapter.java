package com.gat.feature.message.adapter;

import android.support.annotation.IntDef;
import android.view.ViewGroup;

import com.gat.R;
import com.gat.common.adapter.Item;
import com.gat.common.adapter.ItemAdapter;
import com.gat.common.adapter.ItemViewHolder;
import com.gat.feature.message.item.LoadingMessage;
import com.gat.feature.message.item.MessageItem;
import com.gat.feature.message.viewholder.LoadingMessageViewHolder;
import com.gat.feature.message.viewholder.MessageViewHolder;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by ducbtsn on 3/30/17.
 */

public class MessageAdapter extends ItemAdapter {
    @IntDef({MessageAdapter.Type.LOADING, MessageAdapter.Type.MESSAGE})
    @Retention(RetentionPolicy.SOURCE)
    @interface Type {
        int LOADING = 1;
        int MESSAGE    = 2;
    }
    public MessageAdapter() {
        setReady();
    }
    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case Type.MESSAGE:
                return new MessageViewHolder(parent, R.layout.message_item);
            case Type.LOADING:
                return new LoadingMessageViewHolder(parent, R.layout.search_item_loading);
        }
        throw new IllegalArgumentException("Not support type " + viewType);
    }

    @Override
    public @MessageAdapter.Type
    int getItemViewType(int position) {
        Item item = getItemAt(position);
        if(item instanceof LoadingMessage)
            return Type.LOADING;
        if(item instanceof MessageItem)
            return Type.MESSAGE;
        throw new IllegalArgumentException("Not support item " + item);
    }

    public boolean hasLoadMoreItem() {
        if(items.isEmpty())
            return false;
        Item item = items.get(items.size() - 1);
        return item instanceof LoadingMessage && !((LoadingMessage)item).fullHeight();
    }
}
