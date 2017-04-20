package com.gat.data.response.impl;

/**
 * Created by mryit on 4/18/2017.
 */

public class BookReadingInfo {

    private int readingId;
    private int userId;
    private int bookId;
    private int editionId;
    private int readingStatus;
    private String followDate;
    private String startDate;
    private String completeDate;

    public int getReadingId() {
        return readingId;
    }

    public int getUserId() {
        return userId;
    }

    public int getBookId() {
        return bookId;
    }

    public int getEditionId() {
        return editionId;
    }

    public int getReadingStatus() {
        return readingStatus;
    }

    public String getFollowDate() {
        return followDate;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getCompleteDate() {
        return completeDate;
    }

    @Override
    public String toString() {
        return "BookReadingInfo{" +
                "readingId=" + readingId +
                ", userId=" + userId +
                ", bookId=" + bookId +
                ", editionId=" + editionId +
                ", readingStatus=" + readingStatus +
                ", followDate='" + followDate + '\'' +
                ", startDate='" + startDate + '\'' +
                ", completeDate='" + completeDate + '\'' +
                '}';
    }
}
