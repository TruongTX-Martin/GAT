package com.gat.data.response.impl;

import com.gat.data.response.ResponseData;

/**
 * Created by ducbtsn on 3/2/17.
 */

public class ResetPasswordResponseData implements ResponseData {
    public ResetPasswordResponseData(String tokenResetPassword) {
        this.tokenResetPassword = tokenResetPassword;
    }
    private String tokenResetPassword;
    public String tokenResetPassword() {
        return tokenResetPassword;
    }
    public void tokenResetPassword(String tokenResetPassword) {
        this.tokenResetPassword = tokenResetPassword;
    }
    @Override
    public int responseType() {
        return RESPONSE_TYPE.RESET_PASSWORD;
    }
}
