package com.gat.feature.personal.entity;

import com.gat.common.util.Strings;

import org.json.JSONObject;

/**
 * Created by root on 13/04/2017.
 */

public class BookRequestInput {

    //request to you
    private boolean sharingWaitConfirm = true;
    private boolean sharingContacting = true;
    private boolean sharingBorrowing = true;
    private boolean sharingOther = true;

    //request from you
    private boolean borrowWaitConfirm = true;
    private boolean borrowContacting = true;
    private boolean borrowBorrowing = true;
    private boolean borrowOther = true;

    private int page = 1;
    private int per_page = 10;
    private String paramSharing = Strings.EMPTY;
    private String paramBorrow = Strings.EMPTY;

    public BookRequestInput() {
    }

    public String getString() {
        JSONObject object = new JSONObject();
        try {
            object.put("sharingWaitConfirm", sharingWaitConfirm);
            object.put("sharingContacting", sharingContacting);
            object.put("sharingBorrowing", sharingBorrowing);
            object.put("sharingOther", sharingOther);
            object.put("borrowWaitConfirm", borrowWaitConfirm);
            object.put("borrowContacting", borrowContacting);
            object.put("borrowBorrowing", borrowBorrowing);
            object.put("borrowOther", borrowOther);
        } catch (Exception e) {
        }
        return object.toString();
    }
    public static BookRequestInput getObject(String jsonString) {
        BookRequestInput input = new BookRequestInput();
        try {
            JSONObject object = new JSONObject(jsonString);
            if (object.has("sharingWaitConfirm")) {
                boolean sharingWaitConfirm = object.getBoolean("sharingWaitConfirm");
                input.setSharingWaitConfirm(sharingWaitConfirm);
            }
            if (object.has("sharingContacting")) {
                boolean sharingContacting = object.getBoolean("sharingContacting");
                input.setSharingContacting(sharingContacting);
            }
            if (object.has("sharingBorrowing")) {
                boolean sharingBorrowing = object.getBoolean("sharingBorrowing");
                input.setSharingBorrowing(sharingBorrowing);
            }
            if (object.has("sharingOther")) {
                boolean sharingOther = object.getBoolean("sharingOther");
                input.setSharingOther(sharingOther);
            }
            if (object.has("borrowWaitConfirm")) {
                boolean borrowWaitConfirm = object.getBoolean("borrowWaitConfirm");
                input.setBorrowWaitConfirm(borrowWaitConfirm);
            }
            if (object.has("borrowContacting")) {
                boolean borrowContacting = object.getBoolean("borrowContacting");
                input.setBorrowContacting(borrowContacting);
            }
            if (object.has("borrowBorrowing")) {
                boolean borrowBorrowing = object.getBoolean("borrowBorrowing");
                input.setBorrowBorrowing(borrowBorrowing);
            }
            if (object.has("borrowOther")) {
                boolean borrowOther = object.getBoolean("borrowOther");
                input.setBorrowOther(borrowOther);
            }

        } catch (Exception e) {
        }
        return input;
    }

    public BookRequestInput(boolean borrowWaitConfirm, boolean borrowContacting, boolean borrowBorrowing, boolean borrowOther) {
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
