package com.gat.data.exception;

import com.gat.data.response.ResponseData;
import com.gat.data.response.ServerResponse;
import com.gat.data.response.impl.LoginResponseData;

/**
 * Created by Rey on 2/23/2017.
 * Throw when login unsuccessfully.
 */
public class LoginException extends RuntimeException {
    private final ServerResponse responseData;
    public LoginException(ServerResponse responseData) {
        this.responseData = responseData;
    }

    public ServerResponse responseData() {
        return responseData;
    }
}
