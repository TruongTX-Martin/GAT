package com.gat.feature.personal.entity;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

/**
 * Created by truongtechno on 27/03/2017.
 */

public class Data<T> {


    @SerializedName("resultInfo")
    private Object resultInfo;

    public Object getDataInfo() {
        return resultInfo;
    }

    public T getDataReturn(Class<T> t){
        String json = new Gson().toJson(resultInfo);
        T t1 = new Gson().fromJson(json, t);
        return t1;
    }

    //property for book instance
    @SerializedName("totalSharing")
    private int totalSharing;

    @SerializedName("totalNotSharing")
    private int totalNotSharing;

    @SerializedName("lostTotal")
    private int lostTotal;

    public int getTotalSharing() {
        return totalSharing;
    }

    public int getTotalNotSharing() {
        return totalNotSharing;
    }

    public int getLostTotal() {
        return lostTotal;
    }
    //end property for book instances
}
