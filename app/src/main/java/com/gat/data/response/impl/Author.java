package com.gat.data.response.impl;

import com.google.gson.annotations.SerializedName;

/**
 * Created by mryit on 4/18/2017.
 */

public class Author {

    @SerializedName("authorId")
    private int authorId;

    @SerializedName("authorName")
    private String authorName;

    public int getAuthorId() {
        return authorId;
    }

    public String getAuthorName() {
        return authorName;
    }

    @Override
    public String toString() {
        return  authorName + ", ";
    }
}
