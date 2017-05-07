package com.gat.feature.setting.change_password;

import com.rey.mvp2.Presenter;

import io.reactivex.Observable;

/**
 * Created by mozaa on 05/05/2017.
 */

public interface ChangePasswordPresenter extends Presenter {

    void requestChangePassword (String oldPassword, String newPassword, String confirmPassword);
    Observable<String> onChangePasswordSuccess ();
    Observable<String> onChangePasswordFailed ();

}
