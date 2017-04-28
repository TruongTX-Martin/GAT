package com.gat.data.response.impl;

import android.os.Build;
import android.support.annotation.Nullable;
import com.gat.repository.entity.User;
import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;

/**
 * Created by mryit on 4/27/2017.
 */

@AutoValue
public abstract class NotifyEntity {

    public abstract int notificationId();
    public abstract int notificationType();
    public abstract int destId();
    public abstract int sourceId();
    public abstract String sourceName();
    public abstract String sourceImage();
    public abstract int targetId();
    public abstract String targetName();
    public abstract int referId();
    public abstract boolean pullFlag();
    public abstract String modifyTime();


    public static TypeAdapter<NotifyEntity> typeAdapter(Gson gson) {
        return new AutoValue_NotifyEntity.GsonTypeAdapter(gson);
    }

    public static Builder builder() {
        return new AutoValue_NotifyEntity.Builder()
                .notificationId(0)
                .notificationType(0)
                .destId(0)
                .sourceId(0)
                .sourceName("")
                .sourceImage("")
                .targetId(0)
                .targetName("")
                .referId(0)
                .pullFlag(false)
                .modifyTime("");
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder notificationId(int notificationId);
        public abstract Builder notificationType(int notificationType);
        public abstract Builder destId(int destId);
        public abstract Builder sourceId(int sourceId);
        public abstract Builder sourceName(String sourceName);
        public abstract Builder sourceImage(String sourceImage);
        public abstract Builder targetId(int targetId);
        public abstract Builder targetName(String targetName);
        public abstract Builder referId(int referId);
        public abstract Builder pullFlag(boolean pullFlag);
        public abstract Builder modifyTime(String modifyTime);
        public abstract NotifyEntity build();
    }

}
