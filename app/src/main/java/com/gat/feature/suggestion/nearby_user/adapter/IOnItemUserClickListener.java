package com.gat.feature.suggestion.nearby_user.adapter;

import com.gat.repository.entity.UserNearByDistance;

/**
 * Created by mryit on 5/15/2017.
 */

public interface IOnItemUserClickListener {
    void onItemClickListener (int position, UserNearByDistance user);
}
