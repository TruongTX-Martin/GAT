package com.gat.data.response.impl;

import com.google.gson.annotations.SerializedName;

/**
 * Created by mryit on 4/20/2017.
 */

public class Genre {

    @SerializedName("genreId")
    private int genreId;

    public int getGenreId() {
        return genreId;
    }
}
