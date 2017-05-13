package com.gat.feature.suggestion.nearby_user.adapter;

import com.gat.common.adapter.Item;
import com.gat.repository.entity.UserNearByDistance;
import com.google.auto.value.AutoValue;

/**
 * Created by mryit on 5/13/2017.
 */

@AutoValue
public abstract class UserNearByDistanceItem implements Item {

    public static UserNearByDistanceItem instance (UserNearByDistance userNearByDistance) {
        return new AutoValue_UserNearByDistanceItem(userNearByDistance);
    }

    public abstract UserNearByDistance user();

}
