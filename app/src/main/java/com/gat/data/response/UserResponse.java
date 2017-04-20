package com.gat.data.response;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by mozaa on 11/04/2017.
 */

public class UserResponse implements Serializable {

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
}
