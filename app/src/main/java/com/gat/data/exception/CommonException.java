package com.gat.data.exception;

/**
 * Created by ducbtsn on 3/30/17.
 */
public class CommonException extends RuntimeException {
    private final String message;

    public CommonException(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
