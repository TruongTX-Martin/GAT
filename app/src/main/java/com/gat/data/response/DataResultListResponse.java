package com.gat.data.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by mozaa on 11/04/2017.
 */

public class DataResultListResponse<T> {

    @SerializedName("totalResult")
    private int totalResult;

    @SerializedName("resultInfo")
    List<T> resultInfo;

    public int getTotalResult() {
        return totalResult;
    }

    public List<T> getResultInfo() {
        return resultInfo;
    }
}
