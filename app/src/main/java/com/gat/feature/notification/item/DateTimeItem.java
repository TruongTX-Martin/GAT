package com.gat.feature.notification.item;

import com.gat.common.adapter.Item;
import com.google.auto.value.AutoValue;

/**
 * Created by mozaa on 28/04/2017.
 */

@AutoValue
public abstract class DateTimeItem implements Item {

    public static DateTimeItem instance (String timeDisplay) {
        return new AutoValue_DateTimeItem(timeDisplay);
    }

    public abstract String timeDisplay();

}
