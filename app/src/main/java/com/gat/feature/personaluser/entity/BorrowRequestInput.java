package com.gat.feature.personaluser.entity;

/**
 * Created by root on 05/05/2017.
 */

public class BorrowRequestInput {

    private int editionId;
    private int ownerId;

    public BorrowRequestInput() {
    }

    public int getEditionId() {
        return editionId;
    }

    public void setEditionId(int editionId) {
        this.editionId = editionId;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }
}
