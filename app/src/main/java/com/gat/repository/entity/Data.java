package com.gat.repository.entity;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

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

    public List<T> getListDataReturn(Class<T> t) {
        String json = new Gson().toJson(resultInfo);
        List<T> list = new ArrayList<T>();
        try {
            JSONArray jsonArray = new JSONArray(json);
           if(jsonArray != null && jsonArray.length() > 0) {
               for (int i=0; i < jsonArray.length(); i++) {
                   String itemArray = jsonArray.getString(i);
                   T item = new Gson().fromJson(itemArray,t);
                   list.add(item);
               }
           }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
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
