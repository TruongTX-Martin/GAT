package com.gat.repository.entity;

import com.gat.common.util.Strings;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by root on 07/05/2017.
 */

public class UsuallyLocation implements Serializable{

    @SerializedName("userId")
    private int userId;

    @SerializedName("locationId")
    private int locationId;

    @SerializedName("address")
    private String address = Strings.EMPTY;

    @SerializedName("latitude")
    private double latitude;

    @SerializedName("longitude")
    private double longitude;


    public int getUserId() {
        return userId;
    }

    public int getLocationId() {
        return locationId;
    }

    public String getAddress() {
        return address;
    }

}
