package com.gat.feature.book_detail.list_user_sharing_book;

import com.gat.common.adapter.Item;
import com.gat.data.response.UserResponse;
import com.google.auto.value.AutoValue;

/**
 * Created by mozaa on 21/04/2017.
 */

@AutoValue
public abstract class UserSharingItem implements Item {

    public static UserSharingItem instance (UserResponse userResponse) {
        return new AutoValue_UserSharingItem(userResponse);
    }

    public abstract UserResponse user();

}
