package com.gat.feature.register.update.location;

import com.gat.app.screen.Screen;
import com.google.auto.value.AutoValue;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by ducbtsn on 3/19/17.
 */

@AutoValue
public abstract class AddLocationScreen implements Screen{
    @Retention(RetentionPolicy.SOURCE)
    public @interface FROM {
        int LOGIN        = 0;
        int EDIT_INFO    = 1;
    }
    public abstract int requestFrom();
    public static AddLocationScreen instance() {
        return new AutoValue_AddLocationScreen(FROM.LOGIN);
    }

    public static AddLocationScreen instance(int from) {
        return new AutoValue_AddLocationScreen(from);
    }
}
