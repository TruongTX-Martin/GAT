package com.gat.feature.suggestion.nearby_user.adapter;

import android.view.ViewGroup;
import com.gat.R;
import com.gat.common.adapter.Item;
import com.gat.common.adapter.ItemAdapter;
import com.gat.common.adapter.ItemViewHolder;
import com.gat.common.adapter.impl.LoadingItem;
import com.gat.common.adapter.impl.LoadingItemViewHolder;
import com.gat.repository.entity.UserNearByDistance;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mryit on 5/13/2017.
 */

public class UserNearByDistanceAdapter extends ItemAdapter {

    @Retention(RetentionPolicy.SOURCE)
    @interface Type {
        int LOADING = 1;
        int ITEM_USER = 2;
        int ITEM_LOAD_MORE = 3;
    }

    private OnItemLoadMoreClickListener listener;

    public UserNearByDistanceAdapter () {
        setReady();
    }

    public void setOnLoadMoreClickListener (OnItemLoadMoreClickListener callback) {
        listener = callback;
    }

    @Override
    public int getItemViewType(int position) {
        Item item = getItemAt(position);

        if (item instanceof LoadMoreItem) {
            return Type.ITEM_LOAD_MORE;
        } else if (item instanceof UserNearByDistanceItem) {
            return Type.ITEM_USER;
        } else if (item instanceof LoadingItem) {
            return Type.LOADING;
        }

        throw new IllegalArgumentException("NotificationAdapter: Not support item " + item);
    }


    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        ItemViewHolder viewHolder = null;
        switch (viewType) {
            case Type.LOADING:
                viewHolder = new LoadingItemViewHolder(parent, R.layout.search_item_loading);
                break;

            case Type.ITEM_LOAD_MORE:
                viewHolder = new LoadMoreViewHolder(parent, R.layout.item_load_more, listener);
                break;

            case Type.ITEM_USER:
                viewHolder = new UserNearByDistanceViewHolder(parent, R.layout.item_user_near_on_map);
                break;
        }

        return viewHolder;
    }


    public void setItems (List<UserNearByDistance> list, boolean isCanLoadMore) {
        if (list == null || list.isEmpty()) {
            return;
        }

        List<Item> listItems = new ArrayList<>();
        UserNearByDistanceItem userItem;
        for (int i=0,size = list.size(); i < size; i++) {
            userItem = UserNearByDistanceItem.instance(list.get(i));
            listItems.add(userItem);
        }

        if (isCanLoadMore) {
            LoadMoreItem loadMoreItem = LoadMoreItem.instance();
            listItems.add(loadMoreItem);
        }

        if (items != null)
            items.clear();

        setItem(listItems);
        notifyDataSetChanged();
    }


    public void setMoreItems (List<UserNearByDistance> list, boolean isCanLoadMore) {

        items.remove(items.size() -1 );

        List<Item> listItems = new ArrayList<>();
        UserNearByDistanceItem userItem;
        for (int i=0,size = list.size(); i < size; i++) {
            userItem = UserNearByDistanceItem.instance(list.get(i));
            listItems.add(userItem);
        }

        if (isCanLoadMore) {
            LoadMoreItem loadMoreItem = LoadMoreItem.instance();
            listItems.add(loadMoreItem);
        }

        setItem(listItems);
        notifyDataSetChanged();
    }

    protected void clearAllItems () {
        items.removeAll(items);
        notifyDataSetChanged();
    }
}
