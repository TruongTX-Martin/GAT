package com.gat.repository.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by truongtechno on 27/03/2017.
 */

public class Data {

    @SerializedName("resultInfo")
    private UserInfo userInfo;


    public UserInfo getUserInfo() {
        return userInfo;
    }


}
