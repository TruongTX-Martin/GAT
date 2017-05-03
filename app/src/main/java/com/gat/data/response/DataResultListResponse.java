package com.gat.data.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by mozaa on 11/04/2017.
 */

public class DataResultListResponse<T> {

    int totalResult;
    int notifyTotal;

    List<T> resultInfo;

    public int getTotalResult() {
        return totalResult;
    }

    public List<T> getResultInfo() {
        return resultInfo;
    }

    public int getNotifyTotal() {
        return notifyTotal;
    }


    public void setTotalResult(int totalResult) {
        this.totalResult = totalResult;
    }

    public void setNotifyTotal(int notifyTotal) {
        this.notifyTotal = notifyTotal;
    }

    public void setResultInfo(List<T> resultInfo) {
        this.resultInfo = resultInfo;
    }
}
