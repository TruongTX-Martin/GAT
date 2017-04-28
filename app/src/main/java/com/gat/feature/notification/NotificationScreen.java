package com.gat.feature.notification;

import com.gat.app.screen.Screen;
import com.google.auto.value.AutoValue;

/**
 * Created by mryit on 4/27/2017.
 */

@AutoValue
public abstract class NotificationScreen implements Screen {

    public static NotificationScreen instance () {
        return new AutoValue_NotificationScreen();
    }

}
