package com.gat.data.response.impl;

/**
 * Created by mryit on 4/22/2017.
 */

public class BorrowResponse {

    private int recordId;
    private int bookId;
    private int editionId;
    private int instanceId;
    private int borrowerId;
    private int ownerId;

    private int messageId;
    private int recordStatus;

    private String requestTime;
    private String approveTime;
    private String borrowTime;
    private String completeTime;
    private String cancelTime;
    private String rejectTime;

    public int getRecordId() {
        return recordId;
    }

    public void setRecordId(int recordId) {
        this.recordId = recordId;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public int getEditionId() {
        return editionId;
    }

    public void setEditionId(int editionId) {
        this.editionId = editionId;
    }

    public int getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(int instanceId) {
        this.instanceId = instanceId;
    }

    public int getBorrowerId() {
        return borrowerId;
    }

    public void setBorrowerId(int borrowerId) {
        this.borrowerId = borrowerId;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }

    public int getMessageId() {
        return messageId;
    }

    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }

    public int getRecordStatus() {
        return recordStatus;
    }

    public void setRecordStatus(int recordStatus) {
        this.recordStatus = recordStatus;
    }

    public String getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(String requestTime) {
        this.requestTime = requestTime;
    }

    public String getApproveTime() {
        return approveTime;
    }

    public void setApproveTime(String approveTime) {
        this.approveTime = approveTime;
    }

    public String getBorrowTime() {
        return borrowTime;
    }

    public void setBorrowTime(String borrowTime) {
        this.borrowTime = borrowTime;
    }

    public String getCompleteTime() {
        return completeTime;
    }

    public void setCompleteTime(String completeTime) {
        this.completeTime = completeTime;
    }

    public String getCancelTime() {
        return cancelTime;
    }

    public void setCancelTime(String cancelTime) {
        this.cancelTime = cancelTime;
    }

    public String getRejectTime() {
        return rejectTime;
    }

    public void setRejectTime(String rejectTime) {
        this.rejectTime = rejectTime;
    }

    @Override
    public String toString() {
        return "BorrowResponse{" +
                "recordId=" + recordId +
                ", bookId=" + bookId +
                ", editionId=" + editionId +
                ", instanceId=" + instanceId +
                ", borrowerId=" + borrowerId +
                ", ownerId=" + ownerId +
                ", messageId=" + messageId +
                ", recordStatus=" + recordStatus +
                ", requestTime='" + requestTime + '\'' +
                ", approveTime='" + approveTime + '\'' +
                ", borrowTime='" + borrowTime + '\'' +
                ", completeTime='" + completeTime + '\'' +
                ", cancelTime='" + cancelTime + '\'' +
                ", rejectTime='" + rejectTime + '\'' +
                '}';
    }
}
