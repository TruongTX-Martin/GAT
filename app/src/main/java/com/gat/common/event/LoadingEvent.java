package com.gat.common.event;

import android.support.annotation.IntDef;
import android.support.annotation.Nullable;

import com.google.auto.value.AutoValue;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by Rey on 2/15/2017.
 */
@AutoValue
public abstract class LoadingEvent {

    @IntDef({Status.BEGIN, Status.DONE, Status.ERROR, Status.COMPLETE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Status{
        int BEGIN   = 0;
        int DONE    = 1;
        int ERROR   = 2;
        int COMPLETE    = 3;
    }

    public abstract boolean refresh();
    public abstract @Status int status();
    public abstract @Nullable Throwable error();

    public static Builder builder(){
        return new AutoValue_LoadingEvent.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder {

        public abstract Builder refresh(boolean value);
        public abstract Builder status(@Status int value);
        public abstract Builder error(Throwable value);
        public abstract LoadingEvent build();
    }
}
