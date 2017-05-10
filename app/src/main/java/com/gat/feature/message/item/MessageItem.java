package com.gat.feature.message.item;

import com.gat.common.adapter.Item;
import com.gat.repository.entity.Message;
import com.google.auto.value.AutoValue;

/**
 * Created by ducbtsn on 3/30/17.
 */
@AutoValue
public abstract class MessageItem implements Item {
    public abstract Message message();
    public abstract boolean displayDate();
    public abstract boolean displayImage();
    public abstract boolean displayBottomLine();

    public static MessageItem instance(Message message, boolean displayDate, boolean displayImage, boolean displayBottomLine) {
        return new AutoValue_MessageItem(message, displayDate, displayImage,displayBottomLine);
    }
}
