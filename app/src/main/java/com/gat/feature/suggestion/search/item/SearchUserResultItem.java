package com.gat.feature.suggestion.search.item;

import com.gat.common.adapter.Item;
import com.gat.data.response.UserResponse;
import com.google.auto.value.AutoValue;

/**
 * Created by mryit on 4/15/2017.
 */

@AutoValue
public abstract class SearchUserResultItem implements Item {

    public static SearchUserResultItem instance (UserResponse userResponse) {
        return new AutoValue_SearchUserResultItem(userResponse);
    }

    public abstract UserResponse userResponse();

}
