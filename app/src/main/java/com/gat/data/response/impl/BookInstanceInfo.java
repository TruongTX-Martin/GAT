package com.gat.data.response.impl;

/**
 * Created by mryit on 4/18/2017.
 */

public class BookInstanceInfo {

    private int notSharingToal;
    private int sharingTotal;
    private int borrowingTotal;
    private int lostTotal;

    public void setNotSharingToal(int notSharingToal) {
        this.notSharingToal = notSharingToal;
    }

    public void setSharingTotal(int sharingTotal) {
        this.sharingTotal = sharingTotal;
    }

    public void setBorrowingTotal(int borrowingTotal) {
        this.borrowingTotal = borrowingTotal;
    }

    public void setLostTotal(int lostTotal) {
        this.lostTotal = lostTotal;
    }

    public int getNotSharingToal() {
        return notSharingToal;
    }

    public int getSharingTotal() {
        return sharingTotal;
    }

    public int getBorrowingTotal() {
        return borrowingTotal;
    }

    public int getLostTotal() {
        return lostTotal;
    }
}
