package com.gat.feature.message.item;

import android.support.v7.util.DiffUtil;

import com.gat.common.adapter.Item;
import com.gat.common.adapter.ItemResult;
import com.gat.common.util.CommonCheck;
import com.gat.repository.entity.Message;

import java.util.ArrayList;
import java.util.Iterator;
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
        int messageSize = messages.size();
        for (int i = 0; i < messageSize; i++) {
            boolean isLastAdminMessage = false;
            Message message = messages.get(i);
            if (CommonCheck.isAdmin((int)message.userId())) {
                if (i + 1 >= messageSize) {
                    isLastAdminMessage = true;
                } else {
                    if (!CommonCheck.isAdmin((int)(messages.get(i+1).userId()))) {
                        isLastAdminMessage = true;
                    }
                }
            }
            if (CommonCheck.isDiffDay(timeStamp, message.timeStamp()))
                newItems.add(MessageItem.instance(message, true, true, isLastAdminMessage));
            else if (userId == message.userId())
                newItems.add(MessageItem.instance(message, false, false, isLastAdminMessage));
            else
                newItems.add(MessageItem.instance(message, false, true, isLastAdminMessage));
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
        newItems.add(0, MessageItem.instance(data, false, true, false));   // TODO not use now
        return ItemResult.instance(newItems, DiffUtil.calculateDiff(new Comparator(items, newItems)));
    }
}
