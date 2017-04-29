package com.gat.feature.personal.entity;

/**
 * Created by truongtechno on 30/03/2017.
 */

public class BookReadingInput {

    private int userId;
    private boolean readingFilter;
    private boolean toReadFilter;
    private boolean readFilter;

    private int page = 1;
    private int per_page = 10;
    public BookReadingInput(boolean readFilter,boolean readingFilter, boolean toReadFilter) {
        this.readFilter = readFilter;
        this.readingFilter = readingFilter;
        this.toReadFilter = toReadFilter;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPage() {
        return page;
    }

    public int getPer_page() {
        return per_page;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public boolean isReadingFilter() {
        return readingFilter;
    }

    public void setReadingFilter(boolean readingFilter) {
        this.readingFilter = readingFilter;
    }

    public boolean isToReadFilter() {
        return toReadFilter;
    }

    public void setToReadFilter(boolean toReadFilter) {
        this.toReadFilter = toReadFilter;
    }

    public boolean isReadFilter() {
        return readFilter;
    }

    public void setReadFilter(boolean readFilter) {
        this.readFilter = readFilter;
    }
}