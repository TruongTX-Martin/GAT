package com.gat.feature.message.item;

import android.support.v7.util.DiffUtil;

import com.gat.common.adapter.Item;
import com.gat.common.adapter.ItemResult;
import com.gat.common.util.CommonUtil;
import com.gat.repository.entity.Message;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ducbtsn on 3/30/17.
 */

public class MessageItemBuilder extends ItemBuilder<Message>{
    @Override
    public ItemResult addList(List<Item> items, List<Message> messages, boolean clearOldItems, boolean showMore) {
        int oldSize = items.size();
        List<Item> newItems = new ArrayList<>(oldSize + messages.size());

        // Add old items to new list
        if (!clearOldItems) {
            for (Item item : items) {
                if (!(item instanceof LoadingMessage)) {
                    newItems.add(item);
                }
            }
        }
        long timeStamp = 0;
        int userId = 0;
        // Add new messages to new list
        for (Message message : messages) {
            if (CommonUtil.isDiffDay(timeStamp, message.timeStamp()))
                newItems.add(MessageItem.instance(message, true, true));
            else if (userId == message.userId())
                newItems.add(MessageItem.instance(message, false, false));
            else
                newItems.add(MessageItem.instance(message, false, true));
            userId = (int)message.userId();
            timeStamp = message.timeStamp();
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
    public ItemResult updateList(List<Item> items, Message data) {
        int oldSize = items.size();
        List<Item> newItems = new ArrayList<>();

        for (Item item : items) {
            newItems.add(item);
        }
        newItems.add(0, MessageItem.instance(data, false, true));   // TODO not use now
        return ItemResult.instance(newItems, DiffUtil.calculateDiff(new Comparator(items, newItems)));
    }
}
