package com.gat.feature.suggestion.search;

import android.view.ViewGroup;
import com.gat.R;
import com.gat.common.adapter.Item;
import com.gat.common.adapter.ItemAdapter;
import com.gat.common.adapter.ItemViewHolder;
import com.gat.common.adapter.impl.LoadMoreItem;
import com.gat.common.adapter.impl.LoadMoreViewHolder;
import com.gat.common.adapter.impl.LoadingItemViewHolder;
import com.gat.common.adapter.impl.OnItemLoadMoreClickListener;
import com.gat.common.listener.IRecyclerViewItemClickListener;
import com.gat.common.util.MZDebug;
import com.gat.data.response.BookResponse;
import com.gat.data.response.UserResponse;
import com.gat.feature.search.item.LoadingItem;
import com.gat.feature.suggestion.nearby_user.adapter.UserNearByDistanceItem;
import com.gat.feature.suggestion.search.item.SearchBookResultItem;
import com.gat.feature.suggestion.search.item.SearchHistoryItem;
import com.gat.feature.suggestion.search.item.SearchUserResultItem;
import com.gat.feature.suggestion.search.viewholder.SearchBookResultViewHolder;
import com.gat.feature.suggestion.search.viewholder.SearchHistoryViewHolder;
import com.gat.feature.suggestion.search.viewholder.SearchUserResultViewHolder;
import com.gat.repository.entity.UserNearByDistance;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mryit on 4/13/2017.
 */

public class SearchBookAdapter extends ItemAdapter {

    @Retention(RetentionPolicy.SOURCE)
    @interface Type {
        int LOADING = 1;
        int SEARCH  = 2;
        int HISTORY = 3;
        int LOAD_MORE = 4;
    }
    private int mPageType;
    private IRecyclerViewItemClickListener itemClickListener;

    public SearchBookAdapter(int page_type) {
        this.mPageType = page_type;
        setReady();
    }

    public void setOnItemClickListener (IRecyclerViewItemClickListener clickListener) {
        itemClickListener = clickListener;
    }

    private OnItemLoadMoreClickListener loadMoreClickListener;
    public void setOnLoadMoreClickListener (OnItemLoadMoreClickListener callback) {
        loadMoreClickListener = callback;
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
        if (item instanceof LoadMoreItem)
            return Type.LOAD_MORE;

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
                ItemViewHolder finalViewHolderHistory = viewHolder;
                viewHolder.itemView.setOnClickListener(v -> {
                    itemClickListener.onItemClickListener(v, finalViewHolderHistory.getAdapterPosition());
                });
                break;

            case Type.SEARCH:
                if (SuggestSearchActivity.TAB_POS.TAB_USER == mPageType) {
                    viewHolder = new SearchUserResultViewHolder(parent, R.layout.item_search_user_result);
                } else {
                    viewHolder = new SearchBookResultViewHolder(parent, R.layout.item_search_book_result);
                }
                ItemViewHolder finalViewHolderSearch = viewHolder;
                viewHolder.itemView.setOnClickListener(v -> {
                    itemClickListener.onItemClickListener(v, finalViewHolderSearch.getAdapterPosition());
                });
                break;

            case Type.LOAD_MORE:
                viewHolder = new LoadMoreViewHolder(parent, R.layout.item_load_more, loadMoreClickListener);
                break;
        }

        return viewHolder;
    }


    public void addItemLoadMore () {
        LoadMoreItem loadMoreItem = LoadMoreItem.instance();
        if (items != null) {
            items.add(loadMoreItem);
            notifyDataSetChanged();
        } else {
            MZDebug.w(" items trong SaerchBookAdapter = NULL nhé !!");
        }

    }


    public void setItems (List<Item> list) {
        if (items != null) {
            MZDebug.w(" onSearchUserResult set new list items");
            items.clear();
            items.addAll(list);
            notifyDataSetChanged();
        }

    }

    public void setMoreBookItems (List<BookResponse> list) {

        items.remove(items.size() -1 );

        List<Item> listItems = new ArrayList<>();
        SearchBookResultItem bookResultItem;
        for (int i=0,size = list.size(); i < size; i++) {
            bookResultItem = SearchBookResultItem.instance(list.get(i));
            listItems.add(bookResultItem);
        }

        items.addAll(listItems);
        notifyDataSetChanged();
    }


    public void setMoreUserItems (List<UserResponse> list) {

        items.remove(items.size() -1 );

        List<Item> listItems = new ArrayList<>();
        SearchUserResultItem userResultItem;
        for (int i=0,size = list.size(); i < size; i++) {
            userResultItem = SearchUserResultItem.instance(list.get(i));
            listItems.add(userResultItem);
        }

        items.addAll(listItems);
        notifyDataSetChanged();
    }
}
