package com.gat.data.response.impl;

import com.gat.data.response.ResponseData;

/**
 * Created by ducbtsn on 3/2/17.
 */

public class LoginResponseData implements ResponseData {

    public LoginResponseData(String loginToken) {
        this.loginToken = loginToken;
    }

    public String loginToken() {
        return loginToken;
    }

    public void loginToken(String loginToken) {
        this.loginToken = loginToken;
    }

    private String loginToken;

    @Override
    public int responseType() {
        return RESPONSE_TYPE.LOGIN;
    }
}
