package com.gat.data.response;

/**
 * Created by ducbtsn on 4/30/17.
 */

public class SimpleResponse {
    private final String message;

    public SimpleResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
