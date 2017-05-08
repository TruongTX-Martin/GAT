package com.gat.data.exception;

/**
 * Created by ducbtsn on 3/30/17.
 */
public class CommonException extends RuntimeException {

    public static final CommonException SOCKET_TIMEOUT = new CommonException("Socket timeout.");
    public static final CommonException FAILED_RESPONSE = new CommonException("Bad response.");

    private final String message;

    public CommonException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
