package com.gat.data.response.impl;

import com.gat.data.response.BookResponse;
import com.google.gson.annotations.SerializedName;

/**
 * Created by mryit on 4/6/2017.
 */

public class BookSuggest extends BookResponse{

    @SerializedName("sharingCount")
    public int sharingCount;

    public int getSharingCount() {
        return sharingCount;
    }

    @Override
    public String toString() {
        return "BookSuggest{" +
                "sharingCount=" + sharingCount +
                "} " + super.toString();
    }
}
