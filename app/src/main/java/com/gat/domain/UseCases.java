package com.gat.domain;

import android.support.annotation.Nullable;

import com.gat.domain.usecase.UseCase;

/**
 * Created by Rey on 11/18/2016.
 */

public class UseCases {

    /**
     * Release a usecase
     * @param useCase
     * @return always return null.
     */
    public static @Nullable
    <T extends Object> UseCase<T> release(UseCase<T> useCase){
        if(useCase != null)
            useCase.release();
        return null;
    }

    public static void releaseAll(UseCase... useCases){
        for(UseCase useCase : useCases)
            release(useCase);
    }
}
