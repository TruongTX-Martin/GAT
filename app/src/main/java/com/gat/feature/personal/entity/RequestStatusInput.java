package com.gat.feature.personal.entity;

/**
 * Created by root on 29/04/2017.
 */

public class RequestStatusInput {

    private int recordId;
    private int currentStatus;
    private int newStatus;


    public RequestStatusInput() {
    }

    public RequestStatusInput(int recordId, int currentStatus, int newStatus) {
        this.recordId = recordId;
        this.currentStatus = currentStatus;
        this.newStatus = newStatus;
    }

    public int getRecordId() {
        return recordId;
    }

    public void setRecordId(int recordId) {
        this.recordId = recordId;
    }

    public int getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(int currentStatus) {
        this.currentStatus = currentStatus;
    }

    public int getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(int newStatus) {
        this.newStatus = newStatus;
    }
}
