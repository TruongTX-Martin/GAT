package com.gat.data.response.impl;
import com.gat.data.response.BookResponse;
import com.google.auto.value.AutoValue;
import com.google.gson.annotations.SerializedName;

/**
 * Created by mryit on 4/6/2017.
 */
public class BookMostBorrowing extends BookResponse {

    @SerializedName("borrowingCount")
    public int borrowingCount;

    public int getBorrowingCount() {
        return borrowingCount;
    }

    @Override
    public String toString() {
        return "BookMostBorrowing{" +
                "borrowingCount=" + borrowingCount +
                "} " + super.toString();
    }
}