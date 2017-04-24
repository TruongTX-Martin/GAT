package com.gat.data.response.impl;

import com.gat.data.response.ResponseData;

/**
 * Created by ducbtsn on 3/3/17.
 */

public class VerifyTokenResponseData implements ResponseData {
    public VerifyTokenResponseData(String tokenVerify) {
        this.tokenVerify = tokenVerify;
    }
    private String tokenVerify;
    public String tokenVerify() {
        return tokenVerify;
    }
    public void tokenVerify(String tokenVerify) {
        this.tokenVerify = tokenVerify;
    }
    @Override
    public int responseType() {
        return ResponseData.RESPONSE_TYPE.RESET_PASSWORD;
    }
}
