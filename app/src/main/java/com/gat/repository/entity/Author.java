package com.gat.repository.entity;

import com.google.auto.value.AutoValue;

/**
 * Created by Rey on 2/13/2017.
 */
@AutoValue
public abstract class Author {

    public static Author instance(String name){
        return new AutoValue_Author(name);
    }

    public abstract String name();
}
