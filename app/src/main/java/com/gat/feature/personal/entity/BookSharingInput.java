package com.gat.feature.personal.entity;

/**
 * Created by truongtechno on 04/04/2017.
 */

public class BookSharingInput {

    private int instanceId;
    private int sharingStatus;

    public BookSharingInput(int instanceId, int sharingStatus) {
        this.instanceId = instanceId;
        this.sharingStatus = sharingStatus;
    }

    public int getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(int instanceId) {
        this.instanceId = instanceId;
    }

    public int getSharingStatus() {
        return sharingStatus;
    }

    public void setSharingStatus(int sharingStatus) {
        this.sharingStatus = sharingStatus;
    }
}
