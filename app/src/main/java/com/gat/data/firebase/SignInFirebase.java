package com.gat.data.firebase;

import io.reactivex.Observable;

/**
 * Created by ducbtsn on 4/4/17.
 */

public interface SignInFirebase {
    public Observable<Boolean> login();
    public Observable<Boolean> signOut();
    public Observable<Boolean> register();

    public void destroy();
}
