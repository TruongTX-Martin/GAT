package com.gat.repository.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.gat.common.util.Strings;
import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;

/**
 * Created by mozaa on 01/04/2017.
 */
public class UserNearByDistance implements Parcelable{

    private long userId;
    private String name;
    private String imageId;
    private String address;
    private int locationType;
    private float latitude;
    private float longitude;
    private float distance;

    public long getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getImageId() {
        return imageId;
    }

    public String getAddress() {
        return address;
    }

    public int getLocationType() {
        return locationType;
    }

    public float getLatitude() {
        return latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public float getDistance() {
        return distance;
    }

    @Override
    public String toString() {
        return "UserNearByDistance{" +
                "userId=" + userId +
                ", name='" + name + '\'' +
                ", imageId='" + imageId + '\'' +
                ", address='" + address + '\'' +
                ", locationType=" + locationType +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", distance=" + distance +
                '}';
    }


    // "De-parcel object
    private UserNearByDistance(Parcel in) {
        userId = in.readLong();
        name = in.readString();
        imageId = in.readString();
        address = in.readString();
        locationType = in.readInt();
        latitude = in.readFloat();
        longitude = in.readFloat();
        distance = in.readFloat();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(userId);
        dest.writeString(name);
        dest.writeString(imageId);
        dest.writeString(address);
        dest.writeInt(locationType);
        dest.writeFloat(latitude);
        dest.writeFloat(longitude);
        dest.writeFloat(distance);
    }
    // Creator
    public static final Parcelable.Creator
            CREATOR = new Parcelable.Creator() {
        public UserNearByDistance createFromParcel(Parcel in) {
            return new UserNearByDistance(in);
        }

        public UserNearByDistance[] newArray(int size) {
            return new UserNearByDistance[size];
        }
    };
}