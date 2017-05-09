package com.gat.feature.main;

import android.support.annotation.Nullable;

import com.gat.app.screen.Screen;
import com.gat.data.firebase.entity.Notification;
import com.gat.data.firebase.entity.NotificationParcelable;
import com.google.auto.value.AutoValue;

/**
 * Created by mryit on 3/26/2017.
 */

@AutoValue
public abstract class MainScreen implements Screen {
    public abstract @Nullable NotificationParcelable notificationParcelable();
    public static MainScreen instance(NotificationParcelable parcelable) {
        return new AutoValue_MainScreen(parcelable);
    }
    public static MainScreen instance() {
        return new AutoValue_MainScreen(new NotificationParcelable(Notification.NO_NOTIFICATION));
    }
}
