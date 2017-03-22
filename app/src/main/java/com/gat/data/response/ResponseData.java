package com.gat.data.response;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by ducbtsn on 2/26/17.
 */

public interface ResponseData {

    @Retention(RetentionPolicy.SOURCE)
    public @interface RESPONSE_TYPE {
        int NONE            = 0;
        int LOGIN           = 1;
        int RESET_PASSWORD  = 2;
    }

    public static ResponseData NO_RESPONSE = () -> RESPONSE_TYPE.NONE;

    public int responseType();
}
