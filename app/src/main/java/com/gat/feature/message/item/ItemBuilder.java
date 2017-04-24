package com.gat.feature.message.item;

import android.support.v7.util.DiffUtil;

import com.gat.common.adapter.Item;
import com.gat.common.adapter.ItemResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by ducbtsn on 4/1/17.
 */

public abstract class ItemBuilder<T> {
    public static ItemResult defaultItems(){
        Item item = LoadingMessage.instance(LoadingMessage.Message.LOADING, true);
        return ItemResult.instance(Collections.singletonList(item), null);
    }

    public static ItemResult showLoading(List<Item> items, boolean clearOldItems, boolean addIfNeed, @LoadingMessage.Message int error){
        int oldSize = items.size();
        List<Item> newItems = new ArrayList<>(oldSize + 1);

        int index = -1;
        if(!clearOldItems) {
            for (int i = 0; i < oldSize; i++) {
                Item item = items.get(i);
                if (item instanceof LoadingMessage)
                    index = i;
                else
                    newItems.add(item);
            }
        }
        if(index < 0 && addIfNeed)
            index = newItems.size();

        if(index >= 0)
            newItems.add(index, LoadingMessage.instance(error, newItems.isEmpty()));

        return ItemResult.instance(newItems, DiffUtil.calculateDiff(new Comparator(items, newItems)));
    }
    public abstract ItemResult addList(List<Item> items, List<T> data, boolean clearOldItems, boolean showMore);
}
