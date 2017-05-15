package com.gat.feature.suggestion.search.listener;

import com.gat.data.response.BookResponse;
import com.gat.data.response.UserResponse;

import java.util.List;

/**
 * Created by mryit on 5/15/2017.
 */

public interface OnSearchCanLoadMore {

    void onCanLoadMoreItem (int tab_position);

    void onLoadMoreBookWithTitleSuccess (List<BookResponse> list);

    void onLoadMoreBookWithAuthorSuccess (List<BookResponse> list);

    void onLoadMoreUserSuccess (List<UserResponse> list);

}
