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

    public void setReadingId(int readingId) {
        this.readingId = readingId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public void setEditionId(int editionId) {
        this.editionId = editionId;
    }

    public void setReadingStatus(int readingStatus) {
        this.readingStatus = readingStatus;
    }

    public void setFollowDate(String followDate) {
        this.followDate = followDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public void setCompleteDate(String completeDate) {
        this.completeDate = completeDate;
    }

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
