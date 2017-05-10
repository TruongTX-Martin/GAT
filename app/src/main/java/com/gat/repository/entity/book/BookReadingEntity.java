package com.gat.repository.entity.book;

import com.gat.common.util.Strings;
import com.google.gson.annotations.SerializedName;

/**
 * Created by root on 07/04/2017.
 */

public class BookReadingEntity {

    @SerializedName("readingId")
    private int readingId;

    @SerializedName("userId")
    private int userId;

    @SerializedName("bookId")
    private int bookId;

    @SerializedName("editionId")
    private int editionId;

    @SerializedName("title")
    private String title = Strings.EMPTY;

    @SerializedName("author")
    private String author = Strings.EMPTY ;

    @SerializedName("editionImageId")
    private String editionImageId = Strings.EMPTY;

    @SerializedName("rateAvg")
    private double rateAvg;

    @SerializedName("readingStatus")
    private int readingStatus;

    @SerializedName("borrowRecordId")
    private int borrowRecordId;

    @SerializedName("borrowFromUserId")
    private int borrowFromUserId;

    @SerializedName("borrowFromUserName")
    private String borrowFromUserName = Strings.EMPTY;

    @SerializedName("followDate")
    private String followDate = Strings.EMPTY;

    @SerializedName("startDate")
    private String startDate = Strings.EMPTY;

    @SerializedName("completeDate")
    private String completeDate = Strings.EMPTY;

    private boolean isHeader ;

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getEditionImageId() {
        return editionImageId;
    }

    public String getBorrowFromUserName() {
        return borrowFromUserName;
    }

    public int getBorrowFromUserId() {
        return borrowFromUserId;
    }

    public double getRateAvg() {
        return rateAvg;
    }

    public int getReadingStatus() {
        return readingStatus;
    }

    public void setHeader(boolean header) {
        isHeader = header;
    }

    public boolean isHeader() {
        return isHeader;
    }
}
