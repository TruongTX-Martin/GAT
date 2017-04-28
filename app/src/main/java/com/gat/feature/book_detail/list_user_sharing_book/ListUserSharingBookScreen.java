package com.gat.feature.book_detail.list_user_sharing_book;

import com.gat.app.screen.Screen;
import com.gat.data.response.UserResponse;
import com.google.auto.value.AutoValue;

import java.util.List;

/**
 * Created by mozaa on 21/04/2017.
 */

@AutoValue
public abstract class ListUserSharingBookScreen implements Screen {

    public static ListUserSharingBookScreen instance (List<UserResponse> listUser) {
        return new AutoValue_ListUserSharingBookScreen(listUser);
    }

    public abstract List<UserResponse> listUser();

}
