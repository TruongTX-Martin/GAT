package com.gat.feature.search;

import android.support.annotation.IntDef;
import android.view.ViewGroup;

import com.gat.R;
import com.gat.common.adapter.Item;
import com.gat.common.adapter.ItemAdapter;
import com.gat.common.adapter.ItemViewHolder;
import com.gat.feature.search.item.BookItem;
import com.gat.feature.search.item.LoadingItem;
import com.gat.feature.search.viewholder.BookItemViewHolder;
import com.gat.feature.search.viewholder.LoadingItemViewHolder;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by Rey on 2/16/2017.
 */
public class SearchAdapter extends ItemAdapter{

    @IntDef({Type.LOADING, Type.BOOK})
    @Retention(RetentionPolicy.SOURCE)
    @interface Type {
        int LOADING = 1;
        int BOOK    = 2;
    }

    public SearchAdapter(){
        setReady();
    }

    public boolean hasLoadMoreItem(){
        if(items.isEmpty())
            return false;
        Item item = items.get(items.size() - 1);
        return item instanceof LoadingItem && !((LoadingItem)item).fullHeight();
    }

    @Override
    public @Type int getItemViewType(int position) {
        Item item = getItemAt(position);
        if(item instanceof LoadingItem)
            return Type.LOADING;
        if(item instanceof BookItem)
            return Type.BOOK;
        throw new IllegalArgumentException("Not support item " + item);
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, @Type int viewType) {
        switch (viewType) {
            case Type.BOOK:
                return new BookItemViewHolder(parent, R.layout.search_item_book);
            case Type.LOADING:
                return new LoadingItemViewHolder(parent, R.layout.search_item_loading);
        }
        throw new IllegalArgumentException("Not support type " + viewType);
    }
}
