package com.gat.feature.message.item;

import com.gat.common.adapter.Item;
import com.gat.common.adapter.ItemsComparator;
import com.gat.common.util.Objects;

import java.util.List;

/**
 * Created by ducbtsn on 4/1/17.
 */

public class Comparator extends ItemsComparator {

    public Comparator(List<Item> oldItems, List<Item> newItems) {
        super(oldItems, newItems);
    }

    @Override
    protected boolean compareItem(Item oldItem, Item newItem) {
        if(oldItem instanceof LoadingMessage && newItem instanceof LoadingMessage)
            return ((LoadingMessage)oldItem).fullHeight() == ((LoadingMessage)newItem).fullHeight();
        if(oldItem instanceof MessageItem && newItem instanceof MessageItem)
            return Objects.equals(((MessageItem)oldItem).message(), ((MessageItem)newItem).message());
        if(oldItem instanceof GroupItem && newItem instanceof GroupItem)
            return Objects.equals(((GroupItem)oldItem).group().groupId(), ((GroupItem)newItem).group().groupId());
        return super.compareItem(oldItem, newItem);
    }

}