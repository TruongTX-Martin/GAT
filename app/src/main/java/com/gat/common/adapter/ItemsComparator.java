package com.gat.common.adapter;

import android.support.v7.util.DiffUtil;

import com.gat.common.util.Objects;

import java.util.List;

/**
 * Created by Rey on 2/15/2017.
 */

public class ItemsComparator extends DiffUtil.Callback {

    private List<Item> oldItems;
    private List<Item> newItems;

    public ItemsComparator(List<Item> oldItems, List<Item> newItems){
        this.oldItems = oldItems;
        this.newItems = newItems;
    }

    @Override
    public int getOldListSize() {
        return oldItems == null ? 0 : oldItems.size();
    }

    @Override
    public int getNewListSize() {
        return newItems == null ? 0 : newItems.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return compareItem(oldItems.get(oldItemPosition), newItems.get(newItemPosition));
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return compareItemContent(oldItems.get(oldItemPosition), newItems.get(newItemPosition));
    }

    protected boolean compareItem(Item oldItem, Item newItem){
        return Objects.equals(oldItem, newItem);
    }

    protected boolean compareItemContent(Item oldItem, Item newItem){
        return Objects.equals(oldItem, newItem);
    }
}
