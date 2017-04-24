package com.gat.data.exception;

import com.gat.data.response.ResponseData;
import com.gat.data.response.ServerResponse;

/**
 * Created by ducbtsn on 3/3/17.
 */

public class ResponseException extends RuntimeException {
    private final ServerResponse<ResponseData> responseData;

    public ResponseException(ServerResponse<ResponseData> responseData) {
        this.responseData = responseData;
    }

    public ServerResponse<ResponseData> responseData() {
        return responseData;
    }
}
