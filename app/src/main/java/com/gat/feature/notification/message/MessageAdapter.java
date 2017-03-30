package com.gat.feature.notification.message;

import android.support.annotation.IntDef;
import android.view.ViewGroup;

import com.gat.R;
import com.gat.common.adapter.Item;
import com.gat.common.adapter.ItemAdapter;
import com.gat.common.adapter.ItemViewHolder;
import com.gat.feature.notification.message.viewholder.LoadingMessageViewHolder;
import com.gat.feature.search.SearchAdapter;
import com.gat.feature.search.item.BookItem;
import com.gat.feature.search.item.LoadingItem;
import com.gat.feature.search.viewholder.BookItemViewHolder;
import com.gat.feature.search.viewholder.LoadingItemViewHolder;

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
                return new BookItemViewHolder(parent, R.layout.message_item);
            case Type.LOADING:
                return new LoadingMessageViewHolder(parent, R.layout.search_item_loading);
        }
        throw new IllegalArgumentException("Not support type " + viewType);
    }

    @Override
    public @MessageAdapter.Type
    int getItemViewType(int position) {
        Item item = getItemAt(position);
        if(item instanceof LoadingItem)
            return Type.LOADING;
        if(item instanceof BookItem)
            return Type.MESSAGE;
        throw new IllegalArgumentException("Not support item " + item);
    }

    public boolean hasLoadMoreItem() {
        if(items.isEmpty())
            return false;
        Item item = items.get(items.size() - 1);
        return item instanceof LoadingItem && !((LoadingItem)item).fullHeight();
    }
}
