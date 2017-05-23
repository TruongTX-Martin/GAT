package com.gat.data.response;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by mozaa on 11/04/2017.
 */

public class UserResponse implements Parcelable{
    @SerializedName("userId")
    private int userId;

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
    private int editionId;

    @SerializedName("sharingCount")
    private int sharingCount;

    @SerializedName("availableStatus")
    private int availableStatus;

    @SerializedName("requestingStatus")
    private int requestingStatus;

    @SerializedName("recordId")
    private int recordId;

    @SerializedName("recordStatus")
    private int recordStatus;

    @SerializedName("deleteFlag")
    private int deleteFlag;

    protected UserResponse(Parcel in) {
        userId = in.readInt();
        name = in.readString();
        email = in.readString();
        imageId = in.readString();
        userTypeFlag = in.readInt();
        address = in.readString();
        editionId = in.readInt();
        sharingCount = in.readInt();
        availableStatus = in.readInt();
        requestingStatus = in.readInt();
        recordId = in.readInt();
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

    public void setAvailableStatus(int availableStatus) {
        this.availableStatus = availableStatus;
    }

    public void setRequestingStatus(int requestingStatus) {
        this.requestingStatus = requestingStatus;
    }

    public void setRecordId(int recordId) {
        this.recordId = recordId;
    }

    public void setRecordStatus(int recordStatus) {
        this.recordStatus = recordStatus;
    }

    public int getUserId() {
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

    public int getEditionId() {
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

    public int getRecordId() {
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
        dest.writeInt(userId);
        dest.writeString(name);
        dest.writeString(email);
        dest.writeString(imageId);
        dest.writeInt(userTypeFlag);
        dest.writeString(address);
        dest.writeInt(editionId);
        dest.writeInt(sharingCount);
        dest.writeInt(availableStatus);
        dest.writeInt(requestingStatus);
        dest.writeInt(recordId);
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
