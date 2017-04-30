package com.gat.data.response;

import android.support.annotation.IntDef;

import com.gat.common.util.Strings;
import com.gat.data.response.impl.LoginResponseData;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by ducbtsn on 2/26/17.
 */

public class ServerResponse<T> {
    @IntDef({HTTP_CODE.OK, HTTP_CODE.BAD,HTTP_CODE.TOKEN,HTTP_CODE.NONE, HTTP_CODE.SOCKET_EXCEPTION})
    @Retention(RetentionPolicy.SOURCE)
    public @interface HTTP_CODE {
        int NONE = 0;
        int OK = 200;
        int BAD = 400;
        int TOKEN = 401;
        int CONFLICT_DATA = 409;
        int SOCKET_EXCEPTION = 600;
        int GENERAL_EXCEPTION = 700;
    }

    public static ServerResponse BAD_RESPONSE = new ServerResponse("Bad response.", HTTP_CODE.NONE, ResponseData.NO_RESPONSE);
    public static ServerResponse EXCEPTION = new ServerResponse("Exception occurred.", HTTP_CODE.GENERAL_EXCEPTION, ResponseData.NO_RESPONSE);
    public static ServerResponse NO_LOGIN = new ServerResponse(Strings.EMPTY, HTTP_CODE.NONE, new LoginResponseData(Strings.EMPTY));
    public static ServerResponse TOKEN_CHANGED = new ServerResponse("Login token was changed.", HTTP_CODE.TOKEN, new LoginResponseData(Strings.EMPTY));

    String message;
    int code;
    T data;
    public ServerResponse(String message, int code, T data) {
        this.message = message;
        this.code = code;
        this.data = data;
    }
    public String message() {
        return message;
    }
    public void message(String message){
        this.message = message;
    }
    public int code() {
        return this.code;
    }
    public void code(int status) {
        this.code = status;
    }
    public T data() {
        return data;
    }
    public void data(T data) {
        this.data = data;
    }
    public boolean isOk() {
        return (this.code == HTTP_CODE.OK);
    }
}
