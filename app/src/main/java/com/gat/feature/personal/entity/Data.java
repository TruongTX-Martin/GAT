package com.gat.feature.personal.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by truongtechno on 27/03/2017.
 */

public class Data {

//    //    user info
//    @SerializedName("resultInfo")
//    private UserInfo userInfo;
//
//    public UserInfo getUserInfo() {
//        return userInfo;
//    }

    @SerializedName("resultInfo")
    private Object resultInfo;

    public Object getResultInfo() {
        return resultInfo;
    }

    //    //get book instance
//    @SerializedName("resultInfo")
//    private ResultInfo resultInfo;

//    @SerializedName("totalSharing")
//    private int totalSharing;
//
//    @SerializedName("totalNotSharing")
//    private int totalNotSharing;
//
//    @SerializedName("lostTotal")
//    private int lostTotal;

//    public ResultInfo getResultInfo() {
//        return resultInfo;
//    }

//    public int getTotalSharing() {
//        return totalSharing;
//    }
//
//    public int getTotalNotSharing() {
//        return totalNotSharing;
//    }
//
//    public int getLostTotal() {
//        return lostTotal;
//    }
}
