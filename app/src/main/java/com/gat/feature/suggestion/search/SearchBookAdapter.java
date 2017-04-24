package com.gat.feature.suggestion.search;

import android.support.annotation.IntDef;
import android.view.View;
import android.view.ViewGroup;

import com.gat.R;
import com.gat.common.adapter.Item;
import com.gat.common.adapter.ItemAdapter;
import com.gat.common.adapter.ItemViewHolder;
import com.gat.common.adapter.impl.LoadingItemViewHolder;
import com.gat.common.listener.IRecyclerViewItemClickListener;
import com.gat.common.util.MZDebug;
import com.gat.feature.search.item.LoadingItem;
import com.gat.feature.suggestion.search.item.SearchBookResultItem;
import com.gat.feature.suggestion.search.item.SearchHistoryItem;
import com.gat.feature.suggestion.search.item.SearchUserResultItem;
import com.gat.feature.suggestion.search.viewholder.SearchBookResultViewHolder;
import com.gat.feature.suggestion.search.viewholder.SearchHistoryViewHolder;
import com.gat.feature.suggestion.search.viewholder.SearchUserResultViewHolder;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

/**
 * Created by mryit on 4/13/2017.
 */

public class SearchBookAdapter extends ItemAdapter {

    @IntDef({Type.LOADING, Type.SEARCH, Type.HISTORY})
    @Retention(RetentionPolicy.SOURCE)
    @interface Type {
        int LOADING = 1;
        int SEARCH  = 2;
        int HISTORY = 3;
    }
    private int mPageType;
    private IRecyclerViewItemClickListener itemClickListener;

    public SearchBookAdapter(int page_type) {
        this.mPageType = page_type;
        setReady();
    }

    @Override
    public boolean setItem(List<Item> items) {
        return super.setItem(items);
    }

    @Override
    public @Type int getItemViewType(int position) {
        Item item = getItemAt(position);
        if(item instanceof LoadingItem)
            return Type.LOADING;
        if(item instanceof SearchBookResultItem || item instanceof SearchUserResultItem)
            return Type.SEARCH;
        if(item instanceof SearchHistoryItem)
            return Type.HISTORY;

        throw new IllegalArgumentException("Not support item " + item);
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemViewHolder viewHolder = null;

        switch (viewType) {
            case Type.LOADING:
                viewHolder = new LoadingItemViewHolder(parent, R.layout.search_item_loading);
                break;
            case Type.HISTORY:
                viewHolder = new SearchHistoryViewHolder(parent, R.layout.item_search_history, mPageType);
                break;
            case Type.SEARCH:
                if (SuggestSearchActivity.TAB_POS.TAB_USER == mPageType) {
                    viewHolder = new SearchUserResultViewHolder(parent, R.layout.item_search_user_result);
                    break;
                } else {
                    viewHolder = new SearchBookResultViewHolder(parent, R.layout.item_search_book_result);
                    break;
                }
        }

        ItemViewHolder finalViewHolder = viewHolder;
        viewHolder.itemView.setOnClickListener(v -> {
            itemClickListener.onItemClickListener(v, finalViewHolder.getAdapterPosition());
        });

        return viewHolder;
    }

    public void setOnItemClickListener (IRecyclerViewItemClickListener clickListener) {
        itemClickListener = clickListener;
    }

    public boolean hasLoadMoreItem(){
        if(items.isEmpty())
            return false;
        Item item = items.get(items.size() - 1);
        return item instanceof LoadingItem && !((LoadingItem)item).fullHeight();
    }

    protected void clearAllItems () {
        items.removeAll(items);
        notifyDataSetChanged();
    }

}
