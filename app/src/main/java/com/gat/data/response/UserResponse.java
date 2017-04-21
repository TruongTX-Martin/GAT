package com.gat.data.response;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by mozaa on 11/04/2017.
 */

public class UserResponse implements Parcelable {

    @SerializedName("userId")
    private long userId;

    @SerializedName("name")
    private String name;

    @SerializedName("email")
    private String email;

    @SerializedName("imageId")
    private String imageId;

    @SerializedName("userTypeFlag")
    private int userTypeFlag;

    @SerializedName("address")
    private String address;

    @SerializedName("editionId")
    private long editionId;

    @SerializedName("sharingCount")
    private int sharingCount;

    @SerializedName("availableStatus")
    private int availableStatus;

    @SerializedName("requestingStatus")
    private int requestingStatus;

    @SerializedName("recordId")
    private long recordId;

    @SerializedName("recordStatus")
    private int recordStatus;

    @SerializedName("deleteFlag")
    private int deleteFlag;

    protected UserResponse(Parcel in) {
        userId = in.readLong();
        name = in.readString();
        email = in.readString();
        imageId = in.readString();
        userTypeFlag = in.readInt();
        address = in.readString();
        editionId = in.readLong();
        sharingCount = in.readInt();
        availableStatus = in.readInt();
        requestingStatus = in.readInt();
        recordId = in.readLong();
        recordStatus = in.readInt();
        deleteFlag = in.readInt();
    }

    public static final Creator<UserResponse> CREATOR = new Creator<UserResponse>() {
        @Override
        public UserResponse createFromParcel(Parcel in) {
            return new UserResponse(in);
        }

        @Override
        public UserResponse[] newArray(int size) {
            return new UserResponse[size];
        }
    };

    public long getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getImageId() {
        return imageId;
    }

    public int getUserTypeFlag() {
        return userTypeFlag;
    }

    public String getAddress() {
        return address;
    }

    public int getDeleteFlag() {
        return deleteFlag;
    }

    public long getEditionId() {
        return editionId;
    }

    public int getSharingCount() {
        return sharingCount;
    }

    public int getAvailableStatus() {
        return availableStatus;
    }

    public int getRequestingStatus() {
        return requestingStatus;
    }

    public long getRecordId() {
        return recordId;
    }

    public int getRecordStatus() {
        return recordStatus;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(userId);
        dest.writeString(name);
        dest.writeString(email);
        dest.writeString(imageId);
        dest.writeInt(userTypeFlag);
        dest.writeString(address);
        dest.writeLong(editionId);
        dest.writeInt(sharingCount);
        dest.writeInt(availableStatus);
        dest.writeInt(requestingStatus);
        dest.writeLong(recordId);
        dest.writeInt(recordStatus);
        dest.writeInt(deleteFlag);
    }

    @Override
    public String toString() {
        return "UserResponse{" +
                "userId=" + userId +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", imageId='" + imageId + '\'' +
                ", userTypeFlag=" + userTypeFlag +
                ", address='" + address + '\'' +
                ", editionId=" + editionId +
                ", sharingCount=" + sharingCount +
                ", availableStatus=" + availableStatus +
                ", requestingStatus=" + requestingStatus +
                ", recordId=" + recordId +
                ", recordStatus=" + recordStatus +
                ", deleteFlag=" + deleteFlag +
                '}';
    }
}
