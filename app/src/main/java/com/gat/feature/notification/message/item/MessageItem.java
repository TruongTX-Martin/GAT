package com.gat.feature.notification.message.item;

import com.gat.common.adapter.Item;
import com.gat.repository.entity.Message;
import com.google.auto.value.AutoValue;

/**
 * Created by ducbtsn on 3/30/17.
 */
@AutoValue
public abstract class MessageItem implements Item {
    public abstract Message message();

    public static MessageItem instance(Message message) {
        return new AutoValue_MessageItem(message);
    }
}
