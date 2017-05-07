package com.gat.feature.setting.add_email_password;

import com.rey.mvp2.Presenter;

import io.reactivex.Observable;

/**
 * Created by mryit on 5/5/2017.
 */

public interface AddEmailPasswordPresenter extends Presenter {

    void requestAddEmailPassword (String email, String password);
    Observable<String> onSuccess ();
    Observable<String> onFailed ();
    Observable<String> onEmailEmpty ();
    Observable<String> onPasswordEmpty ();

}
