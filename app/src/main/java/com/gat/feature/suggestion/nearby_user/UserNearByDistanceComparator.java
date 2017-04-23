package com.gat.feature.suggestion.nearby_user;

import android.support.annotation.NonNull;

import com.gat.repository.entity.UserNearByDistance;

import java.util.Comparator;

/**
 * Created by mryit on 4/16/2017.
 */

public class UserNearByDistanceComparator implements Comparator<UserNearByDistance>, Comparable<UserNearByDistance> {


    @Override
    public int compare(UserNearByDistance o1, UserNearByDistance o2) {
        return (int) (o1.getDistance() - o2.getDistance());
    }

    @Override
    public int compareTo(@NonNull UserNearByDistance o) {
        return 0;
    }
}
