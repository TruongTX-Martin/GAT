package com.gat.repository.entity;

import android.os.Parcel;
import android.os.Parcelable;

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
    private long sharingCount;
    private long readCount;


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

    public long getSharingCount() {
        return sharingCount;
    }

    public long getReadCount() {
        return readCount;
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
        sharingCount = in.readLong();
        readCount = in.readLong();
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
        dest.writeLong(sharingCount);
        dest.writeLong(readCount);
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
                ", sharingCount=" + sharingCount +
                ", readCount=" + readCount +
                '}';
    }
}