package com.gat.data.firebase;

import io.reactivex.Observable;

/**
 * Created by ducbtsn on 4/4/17.
 */

public interface SignInFirebase {
    public void login();
    public void signOut();
    public void register();

    public Observable<Boolean> getLoginResult();

    public void destroy();
}
