package com.gat.repository.entity;

import com.google.auto.value.AutoValue;

/**
 * Created by ducbtsn on 3/28/17.
 */

@AutoValue
public abstract class Message {
    public abstract String userId();
    public abstract String name();
    public abstract String message();
    public abstract String imageId();
    public abstract String time();

    public static Message instance(String userId, String name, String message, String imageId, String time) {
        return new AutoValue_Message(userId, name, message, imageId, time);
    }
}
