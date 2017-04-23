package com.gat.feature.personal.entity;

import com.gat.common.util.Strings;

/**
 * Created by root on 13/04/2017.
 */

public class BookRequestInput {

    //request to you
    private boolean sharingWaitConfirm;
    private boolean sharingContacting;
    private boolean sharingBorrowing;
    private boolean sharingOther;

    //request from you
    private boolean borrowWaitConfirm;
    private boolean borrowContacting;
    private boolean borrowBorrowing;
    private boolean borrowOther;

    private int page = 1;
    private int per_page = 10;
    private String paramSharing = Strings.EMPTY;
    private String paramBorrow = Strings.EMPTY;

    public BookRequestInput(boolean borrowWaitConfirm,boolean borrowContacting,boolean borrowBorrowing,boolean borrowOther) {
        this.borrowWaitConfirm = borrowWaitConfirm;
        this.borrowContacting = borrowContacting;
        this.borrowBorrowing = borrowBorrowing;
        this.borrowOther = borrowOther;
    }

    public String getParamSharing() {
        paramSharing = sharingWaitConfirm+ "," + sharingContacting + "," + sharingBorrowing + "," + sharingOther;
        return paramSharing;
    }

    public String getParamBorrow() {
        paramBorrow = borrowWaitConfirm + "," + borrowContacting + "," + borrowBorrowing + "," + borrowOther;
        return paramBorrow;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPer_page() {
        return per_page;
    }

    public boolean isSharingWaitConfirm() {
        return sharingWaitConfirm;
    }

    public void setSharingWaitConfirm(boolean sharingWaitConfirm) {
        this.sharingWaitConfirm = sharingWaitConfirm;
    }

    public boolean isSharingContacting() {
        return sharingContacting;
    }

    public void setSharingContacting(boolean sharingContacting) {
        this.sharingContacting = sharingContacting;
    }

    public boolean isSharingBorrowing() {
        return sharingBorrowing;
    }

    public void setSharingBorrowing(boolean sharingBorrowing) {
        this.sharingBorrowing = sharingBorrowing;
    }

    public boolean isSharingOther() {
        return sharingOther;
    }

    public void setSharingOther(boolean sharingOther) {
        this.sharingOther = sharingOther;
    }

    public boolean isBorrowWaitConfirm() {
        return borrowWaitConfirm;
    }

    public void setBorrowWaitConfirm(boolean borrowWaitConfirm) {
        this.borrowWaitConfirm = borrowWaitConfirm;
    }

    public boolean isBorrowContacting() {
        return borrowContacting;
    }

    public void setBorrowContacting(boolean borrowContacting) {
        this.borrowContacting = borrowContacting;
    }

    public boolean isBorrowBorrowing() {
        return borrowBorrowing;
    }

    public void setBorrowBorrowing(boolean borrowBorrowing) {
        this.borrowBorrowing = borrowBorrowing;
    }

    public boolean isBorrowOther() {
        return borrowOther;
    }

    public void setBorrowOther(boolean borrowOther) {
        this.borrowOther = borrowOther;
    }
}
