package com.gat.repository.entity;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by Rey on 2/23/2017.
 */

public interface LoginData {
    @Retention(RetentionPolicy.SOURCE)
    public @interface Type {
        int NONE        = 0;
        int FACE        = 1;
        int GOOGLE      = 2;
        int TWITTER     = 3;
        int EMAIL       = 4;
    }
    int type();

    public static LoginData EMPTY = () -> Type.NONE;
}
