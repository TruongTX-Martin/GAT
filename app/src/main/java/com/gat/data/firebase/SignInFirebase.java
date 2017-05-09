package com.gat.data.firebase;

import com.gat.repository.entity.LoginData;
import com.google.firebase.auth.FirebaseUser;

import io.reactivex.Observable;

/**
 * Created by ducbtsn on 4/4/17.
 */

public interface SignInFirebase {
    public void login();
    public void signOut();
    public void register();

    public Observable<Boolean> getLoginResult();

    public Observable<Boolean> linkWithCredential(LoginData loginData);

    public Observable<Boolean> unlinkWithCredential(int type);

    public void destroy();
}
