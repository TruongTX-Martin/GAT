package com.gat.data.share;

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

    public Observable<Integer> getBadgeSubject() {
        return badgeChangeSubject;
    }
}