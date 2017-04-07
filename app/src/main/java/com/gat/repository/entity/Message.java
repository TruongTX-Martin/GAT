package com.gat.repository.entity;

import com.gat.common.util.Strings;

/**
 * Created by ducbtsn on 3/28/17.
 */
public class Message {
    private Long userId;
    private String sender;
    private String message;
    private String imageId;
    private Long timeStamp;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public Long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    private String groupId;

    public Message() {

    }
    public Message(Long userId, String name, String message, String imageId, Long timeStamp) {
        this.userId = userId;
        this.sender = name;
        this.message = message;
        this.imageId = imageId;
        this.timeStamp = timeStamp;
    }

    public static Message NONE = new Message(
            0l,
            Strings.EMPTY,
            Strings.EMPTY,
            Strings.EMPTY,
            0l
    );
}
