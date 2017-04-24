package com.gat.data.response;

import java.util.List;

/**
 * Created by mozaa on 02/04/2017.
 */

public class ResultInfoList<T> {

    List<T> resultInfo;

    public ResultInfoList(List<T> list) {
        this.resultInfo = list;
    }

    public void setResultInfo(List<T> data) {
        this.resultInfo = data;
    }

    public List<T> getResultInfo() {
        return resultInfo;
    }

}
