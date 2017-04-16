package com.gat.feature.personal.entity;

/**
 * Created by truongtechno on 30/03/2017.
 */

public class BookInstanceInput {


    private boolean sharingFilter;
    private boolean notSharingFilter;
    private boolean lostFilter;
    private int page = 1;
    private int per_page = 10;



    public BookInstanceInput(boolean sharingFilter, boolean notSharingFilter, boolean lostFilter) {
        this.sharingFilter = sharingFilter;
        this.notSharingFilter = notSharingFilter;
        this.lostFilter = lostFilter;
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

    public boolean isSharingFilter() {
        return sharingFilter;
    }

    public void setSharingFilter(boolean sharingFilter) {
        this.sharingFilter = sharingFilter;
    }

    public boolean isNotSharingFilter() {
        return notSharingFilter;
    }

    public void setNotSharingFilter(boolean notSharingFilter) {
        this.notSharingFilter = notSharingFilter;
    }

    public boolean isLostFilter() {
        return lostFilter;
    }

    public void setLostFilter(boolean lostFilter) {
        this.lostFilter = lostFilter;
    }
}
