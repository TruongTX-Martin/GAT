package com.gat.repository.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by truongtechno on 27/03/2017.
 */

public class UserInfo {

    @SerializedName("userId")
    private int userId;

    @SerializedName("name")
    private String name;

    @SerializedName("email")
    private String email;

    @SerializedName("imageId")
    private String imageid;

    @SerializedName("userTypeFlag")
    private int userTypeFlag;

    @SerializedName("deleteFlag")
    private int deleteFlag;

    @SerializedName("loanCount")
    private int loanCount;

    @SerializedName("readCount")
    private int readCount;

    @SerializedName("requestCount")
    private int requestCount;


    public String getName() {
        return name;
    }

}
