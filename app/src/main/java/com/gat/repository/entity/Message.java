package com.gat.repository.entity;

import com.gat.common.util.Strings;
import com.google.auto.value.AutoValue;

/**
 * Created by ducbtsn on 3/28/17.
 */
@AutoValue
public abstract class Message {
    public abstract long userId();
    public abstract String message();
    public abstract long timeStamp();
    public abstract boolean isRead();
    public abstract String groupId();

    public static Builder builder() {
        return new AutoValue_Message.Builder()
                .userId(0l)
                .message(Strings.EMPTY)
                .timeStamp(0l)
                .isRead(false)
                .groupId(Strings.EMPTY);
    }

    public static Message instance() {
        return new AutoValue_Message.Builder()
                .userId(0l)
                .message(Strings.EMPTY)
                .timeStamp(0l)
                .isRead(false)
                .groupId(Strings.EMPTY)
                .build();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder userId(long userId);
        public abstract Builder message(String message);
        public abstract Builder timeStamp(long timeStamp);
        public abstract Builder isRead(boolean isRead);
        public abstract Builder groupId(String groupId);
        public abstract Message build();
    }
}
