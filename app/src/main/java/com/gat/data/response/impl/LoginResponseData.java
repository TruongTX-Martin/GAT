package com.gat.data.response.impl;

import android.support.annotation.Nullable;

import com.gat.data.response.ResponseData;

/**
 * Created by ducbtsn on 3/2/17.
 */

public class LoginResponseData implements ResponseData {

    public LoginResponseData(String loginToken, String firebasePassword) {
        this.loginToken = loginToken;
        this.firebasePassword = firebasePassword;
    }

    public String loginToken() {
        return loginToken;
    }

    public void loginToken(String loginToken) {
        this.loginToken = loginToken;
    }

    private String loginToken;

    public String getFirebasePassword() {
        return firebasePassword;
    }

    public void setFirebasePassword(String firebasePassword) {
        this.firebasePassword = firebasePassword;
    }

    private @Nullable String firebasePassword;

    @Override
    public int responseType() {
        return RESPONSE_TYPE.LOGIN;
    }
}
