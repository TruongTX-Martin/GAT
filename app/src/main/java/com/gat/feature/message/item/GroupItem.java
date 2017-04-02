package com.gat.feature.message.item;

import com.gat.common.adapter.Item;
import com.gat.repository.entity.Group;
import com.google.auto.value.AutoValue;

/**
 * Created by ducbtsn on 4/1/17.
 */
@AutoValue
public abstract class GroupItem implements Item{
    public abstract Group group();
    public static GroupItem instance(Group group) {
        return new AutoValue_GroupItem(group);
    }
}
