package com.gat.feature.suggestion.item;

import com.gat.common.adapter.Item;
import com.gat.repository.entity.UserNearByDistance;
import com.google.auto.value.AutoValue;

/**
 * Created by mryit on 4/3/2017.
 */
@AutoValue
public abstract class UserItem implements Item{

    public UserItem instance (UserNearByDistance userNearByDistance) {
        return new AutoValue_UserItem(userNearByDistance);
    }

    public abstract UserNearByDistance user();

}
