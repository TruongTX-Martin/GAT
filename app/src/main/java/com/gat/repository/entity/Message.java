package com.gat.repository.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by root on 19/04/2017.
 */

public class Message {

    @SerializedName("message")
    private String message;


    public String getMessage() {
        return message;
    }
}
