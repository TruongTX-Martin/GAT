package com.gat.data.firebase.entity;

import android.support.annotation.Nullable;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;

/**
 * Created by ducbtsn on 5/6/17.
 */
@AutoValue
public abstract class Notification {
    public abstract @Nullable String title();
    public abstract String message();
    public abstract int pushType();
    public abstract String sound();
    public abstract int badge();
    public abstract @Nullable Integer senderId();
    public abstract @Nullable Integer requestId();

    public static Notification instance(String title, String message, int pushType, String sound, int badge, int senderId, int requestId) {
        return new AutoValue_Notification(title, message, pushType, sound, badge, senderId, requestId);
    }

    public static TypeAdapter<Notification> typeAdapter(Gson gson) {
        return new AutoValue_Notification.GsonTypeAdapter(gson);
    }
}
