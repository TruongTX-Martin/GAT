package com.gat.feature.message;

import com.gat.app.screen.Screen;
import com.google.auto.value.AutoValue;

/**
 * Created by ducbtsn on 3/27/17.
 */

@AutoValue
public abstract class MessageScreen implements Screen {
    public abstract String userId();
    public static MessageScreen instance(String userId) {
        return new AutoValue_MessageScreen(userId);
    }
}
