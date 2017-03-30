package com.gat.feature.notification.message;

import com.gat.app.screen.Screen;
import com.google.auto.value.AutoValue;

/**
 * Created by ducbtsn on 3/27/17.
 */

@AutoValue
public abstract class MessageScreen implements Screen {
    public static MessageScreen instance() {
        return new AutoValue_MessageScreen();
    }
}
