package com.gat.data.exception;

import com.gat.data.response.ServerResponse;

/**
 * Created by mozaa on 18/05/2017.
 */

public class ConflictDataException extends RuntimeException {

    private final ServerResponse responseData;

    public ConflictDataException(ServerResponse responseData) {
        this.responseData = responseData;
    }

    public ServerResponse responseData() {
        return responseData;
    }

}
