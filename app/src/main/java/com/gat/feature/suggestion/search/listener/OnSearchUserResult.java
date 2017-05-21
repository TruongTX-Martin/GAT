package com.gat.feature.suggestion.search.listener;

import com.gat.data.response.DataResultListResponse;
import com.gat.data.response.UserResponse;

import java.util.List;

/**
 * Created by mozaa on 11/04/2017.
 */

public interface OnSearchUserResult {

    void onRequestSearchUser ();

    void onSearchUserResult (List<UserResponse> list);

    void onSearchUserResultTotal (Integer total);

}
