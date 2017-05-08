package com.gat.data.response.impl;

/**
 * Created by mryit on 4/18/2017.
 */

public class BookInstanceInfo {

    private int notSharingTotal;
    private int sharingTotal;
    private int borrowingTotal;
    private int lostTotal;

    public void setSharingTotal(int sharingTotal) {
        this.sharingTotal = sharingTotal;
    }

    public void setBorrowingTotal(int borrowingTotal) {
        this.borrowingTotal = borrowingTotal;
    }

    public void setLostTotal(int lostTotal) {
        this.lostTotal = lostTotal;
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

    public int getNotSharingTotal() {
        return notSharingTotal;
    }

    public void setNotSharingTotal(int notSharingTotal) {
        this.notSharingTotal = notSharingTotal;
    }

    @Override
    public String toString() {
        return "BookInstanceInfo{" +
                "notSharingTotal=" + notSharingTotal +
                ", sharingTotal=" + sharingTotal +
                ", borrowingTotal=" + borrowingTotal +
                ", lostTotal=" + lostTotal +
                '}';
    }
}
