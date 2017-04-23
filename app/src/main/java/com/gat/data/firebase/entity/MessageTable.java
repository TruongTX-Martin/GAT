package com.gat.data.firebase.entity;

/**
 * Created by ducbtsn on 4/18/17.
 */

public class MessageTable {
    private String message;
    private long userId;
    private long timeStamp;
    private boolean isRead;
    private String groupId;

    public MessageTable() {

    }
    public MessageTable (long userId, String message, long timeStamp, boolean isRead) {
        this.userId = userId;
        this.message = message;
        this.timeStamp = timeStamp;
        this.isRead = isRead;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
}
