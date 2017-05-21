package com.gat.data.share;

import com.gat.data.user.UserAddressData;
import com.gat.repository.entity.UsuallyLocation;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.Subject;

/**
 * Created by ducbtsn on 5/8/17.
 */

public class SharedData {

    private static SharedData mSharedData;

    private static int mBadge = 0;

    private static Subject<Integer> badgeChangeSubject;

    private static boolean isLoggedIn = false;

    private Integer mMessagingUserId = 0;

    private List<Integer> categoryList;

    private UserAddressData usuallyLocation;

    private SharedData() {

    }

    public static SharedData getInstance() {
        if (mSharedData == null) {
            synchronized (SharedData.class) {
                if (mSharedData == null) {
                    mSharedData = new SharedData();
                    badgeChangeSubject = BehaviorSubject.createDefault(0);
                }
            }
        }
        return mSharedData;
    }

    public void setBadge(int badge) {
        synchronized (SharedData.class) {
            mBadge = badge;
            badgeChangeSubject.onNext(mBadge);
        }
    }

    public int getBadge() {
        synchronized (SharedData.class) {
            return mBadge;
        }
    }

    public int getMessagingUserId() {
        synchronized (mMessagingUserId) {
            return mMessagingUserId;
        }
    }

    public void setMessagingUserId(int userId) {
        synchronized (mMessagingUserId) {
            mMessagingUserId = userId;
        }
    }

    public Observable<Integer> getBadgeSubject() {
        return badgeChangeSubject;
    }

    public void setCategoryList(List<Integer> categoryList) {
        this.categoryList = categoryList;
    }

    public List<Integer> getCategoryList() {
        return this.categoryList;
    }

    public void setUsuallyLocation(UserAddressData usuallyLocation) {
        this.usuallyLocation = usuallyLocation;
    }

    public UserAddressData getUsuallyLocation() {
        return this.usuallyLocation;
    }
}