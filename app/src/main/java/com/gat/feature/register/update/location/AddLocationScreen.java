package com.gat.feature.register.update.location;

import com.gat.app.screen.Screen;
import com.google.auto.value.AutoValue;

/**
 * Created by ducbtsn on 3/19/17.
 */

@AutoValue
public abstract class AddLocationScreen implements Screen{
    public static AddLocationScreen instance() {
        return new AutoValue_AddLocationScreen();
    }
}
