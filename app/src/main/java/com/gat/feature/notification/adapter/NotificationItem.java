package com.gat.feature.notification.adapter;

import com.gat.common.adapter.Item;
import com.gat.data.response.impl.NotifyEntity;
import com.google.auto.value.AutoValue;

/**
 * Created by mozaa on 28/04/2017.
 */

@AutoValue
public abstract class NotificationItem implements Item{

    public static NotificationItem instance (NotifyEntity notifyEntity) {
        return new AutoValue_NotificationItem(notifyEntity);
    }

    public abstract NotifyEntity notifyEntity();

}
