package com.gat.feature.suggestion.search.item;

import com.gat.common.adapter.Item;
import com.google.auto.value.AutoValue;

/**
 * Created by mryit on 4/15/2017.
 */
@AutoValue
public abstract class SearchHistoryItem implements Item{

    public static SearchHistoryItem instance (String keyword) {
        return new AutoValue_SearchHistoryItem(keyword);
    }

    public abstract String keyword ();

}
