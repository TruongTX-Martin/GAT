package com.gat.feature.login;

import com.gat.data.response.ResponseData;
import com.gat.data.response.ServerResponse;
import com.gat.data.response.impl.LoginResponseData;
import com.gat.data.response.impl.ResetPasswordResponseData;
import com.gat.data.response.impl.VerifyTokenResponseData;
import com.gat.repository.entity.User;
import com.rey.mvp2.Presenter;
import com.gat.repository.entity.LoginData;

import io.reactivex.Observable;

/**
 * Created by ducbtsn on 2/23/17.
 */

public interface LoginPresenter extends Presenter {
    Observable<User> loginResult();
    Observable<String> onError();
    void setIdentity(LoginData loginData);

    void sendRequestReset(String email);
    Observable<ServerResponse<ResetPasswordResponseData>> resetPasswordResponse();

    Observable<ServerResponse<VerifyTokenResponseData>> verifyResult();
    void verifyToken(String token);

    Observable<ServerResponse<LoginResponseData>> changePasswordResult();
    void changePassword(String password);

    Observable<Boolean> loginFirebase();
    void loginOnFirebase();

    // No use
    void loadLocalUser();
    Observable<LoginData> loadLocalLoginData();
    void login();

    void init();
}
