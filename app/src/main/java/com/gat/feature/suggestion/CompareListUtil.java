package com.gat.feature.suggestion;

import com.gat.repository.entity.UserNearByDistance;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by mryit on 4/16/2017.
 */

public class CompareListUtil {

    public static List<UserNearByDistance> differentOnNewList(List<UserNearByDistance> oldList, List<UserNearByDistance> newList) {
        List<UserNearByDistance> destinationList = new ArrayList<>(newList);
        destinationList.removeAll( oldList );

        return destinationList;
    }

//    public static <T extends Object> List<T> destinationList (List<T> oldList, List<T> newList) {
//
//        List<T> destinationList = new ArrayList<T>(newList);
//        destinationList.removeAll( oldList );
//
//        return destinationList;
//    }

    public static <T extends Collection> List<T> addTwoList (List<T> oldList, List<T> newList) {

        List<T> destinationList = new ArrayList<T>(newList);
        destinationList.removeAll( oldList );
        oldList.addAll(destinationList);

        return oldList;
    }


    public static List<UserNearByDistance> destinationListUserNear(List<UserNearByDistance> oldList, List<UserNearByDistance> newList) {
        List<UserNearByDistance> commonList = new ArrayList<>();

        // nếu user id của newList không có trong oldList thì nó là destinationList
        for (UserNearByDistance user : newList) {
            for (UserNearByDistance userOld : oldList) {
                if (user.getUserId() == userOld.getUserId()) {
                    commonList.add(user);
                    break;
                }
            }
        }

        newList.removeAll(commonList);

        return newList;
    }



}
