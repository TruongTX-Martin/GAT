package com.gat.data.response;

import com.google.gson.annotations.SerializedName;

/**
 * Created by mozaa on 11/04/2017.
 */

public class UserResponse {

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
}
