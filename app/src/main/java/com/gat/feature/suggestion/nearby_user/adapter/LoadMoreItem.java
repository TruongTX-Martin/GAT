package com.gat.feature.suggestion.nearby_user.adapter;

import com.gat.common.adapter.Item;
import com.google.auto.value.AutoValue;

/**
 * Created by mryit on 5/13/2017.
 */

@AutoValue
public abstract class LoadMoreItem implements Item{

    public static LoadMoreItem instance () {
        return new AutoValue_LoadMoreItem();
    }

}
