package com.gat.repository.entity;

import android.support.annotation.Nullable;

import com.google.auto.value.AutoValue;

/**
 * Created by Rey on 2/14/2017.
 */
@AutoValue
public abstract class Cover {

    public abstract String url();
    public abstract @Nullable String description();

    public static Builder builder(){
        return new AutoValue_Cover.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder {

        public abstract Builder url(String value);
        public abstract Builder description(String value);
        public abstract Cover build();

    }
}
