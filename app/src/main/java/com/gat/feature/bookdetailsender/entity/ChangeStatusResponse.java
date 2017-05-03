package com.gat.feature.bookdetailsender.entity;

import com.gat.common.util.Strings;

/**
 * Created by root on 03/05/2017.
 */

public class ChangeStatusResponse {

    private String message = Strings.EMPTY;
    private int statusCode;


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }
}
