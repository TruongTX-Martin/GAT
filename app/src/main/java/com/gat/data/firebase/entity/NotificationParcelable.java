package com.gat.data.firebase.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ducbtsn on 5/6/17.
 */

public class NotificationParcelable implements Parcelable {

    private final Notification notification;

    public NotificationParcelable(Notification notification) {
        this.notification = notification;
    }

    NotificationParcelable(Parcel in) {
        String title = in.readString();
        String message = in.readString();
        int pushType = in.readInt();
        String sound = in.readString();
        int badge = in.readInt();
        int senderId = in.readInt();
        int requestId = in.readInt();
        this.notification = Notification.instance(title, message, pushType, sound, badge, senderId, requestId);
    }

    public static final Creator<NotificationParcelable> CREATOR = new Creator<NotificationParcelable>() {
        @Override
        public NotificationParcelable createFromParcel(Parcel in) {
            return new NotificationParcelable(in);
        }

        @Override
        public NotificationParcelable[] newArray(int size) {
            return new NotificationParcelable[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(notification.title());
        dest.writeString(notification.message());
        dest.writeInt(notification.pushType());
        dest.writeString(notification.sound());
        dest.writeInt(notification.badge());
        dest.writeInt(notification.senderId());
        dest.writeInt(notification.requestId());
    }

    public Notification getNotification() {
        return notification;
    }
}