package com.gat.feature.message.item;

import android.support.v7.util.DiffUtil;

import com.gat.common.adapter.Item;
import com.gat.common.adapter.ItemResult;
import com.gat.repository.entity.Group;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ducbtsn on 4/1/17.
 */

public class GroupItemBuilder extends ItemBuilder<Group> {

    @Override
    public ItemResult addList(List<Item> items, List<Group> groups, boolean clearOldItems, boolean showMore) {
        int oldSize = items.size();
        List<Item> newItems = new ArrayList<>(oldSize + groups.size());

        // Add old items to new list
        if (!clearOldItems) {
            for (Item item : items) {
                if (!(item instanceof LoadingMessage)) {
                    newItems.add(item);
                }
            }
        }
        // Add new messages to new list
        for (Group group : groups) {
            newItems.add(GroupItem.instance(group));
        }
        // Check new list
        if (newItems.isEmpty()) {
            newItems.add(LoadingMessage.instance(LoadingMessage.Message.EMPTY, true));
        } else if (showMore) {
            newItems.add(LoadingMessage.instance(LoadingMessage.Message.LOADING, false));
        }

        return ItemResult.instance(newItems, DiffUtil.calculateDiff(new Comparator(items, newItems)));
    }

    @Override
    public ItemResult updateList(List<Item> items, Group data) {
        List<Item> newItems = new ArrayList<>();

        for (Item item : items) {
            if (item instanceof  GroupItem && !((GroupItem) item).group().groupId().equals(data.groupId())) {
                newItems.add(item);
            }
        }
        newItems.add(0, GroupItem.instance(data));
        return ItemResult.instance(newItems, DiffUtil.calculateDiff(new Comparator(items, newItems)));
    }
}
