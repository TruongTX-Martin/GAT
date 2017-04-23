package com.gat.feature.personaluser.entity;

/**
 * Created by root on 20/04/2017.
 */

public class BookSharingUserInput {

    private int ownerId;
    private int userId;
    private int page = 1;
    private int per_page = 10;


    public BookSharingUserInput() {
    }


    public int getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
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

    public void setPer_page(int per_page) {
        this.per_page = per_page;
    }
}
