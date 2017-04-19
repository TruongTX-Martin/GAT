package com.gat.data.response.impl;

import com.google.gson.annotations.SerializedName;

/**
 * Created by mryit on 4/18/2017.
 */

public class Translator {

    @SerializedName("editionId")
    private int editionId;

    @SerializedName("authorName")
    private String authorName;

    public int getEditionId() {
        return editionId;
    }

    public String getAuthorName() {
        return authorName;
    }
}
