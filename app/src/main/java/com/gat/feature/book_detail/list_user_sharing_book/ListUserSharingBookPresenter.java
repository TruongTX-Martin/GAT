package com.gat.feature.book_detail.list_user_sharing_book;

import com.gat.data.response.UserResponse;
import com.rey.mvp2.Presenter;

import java.util.List;

/**
 * Created by mozaa on 21/04/2017.
 */

public interface ListUserSharingBookPresenter extends Presenter {

    void setListUser(List<UserResponse> list);

}
