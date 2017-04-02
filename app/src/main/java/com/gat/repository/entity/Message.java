package com.gat.repository.entity;

/**
 * Created by ducbtsn on 3/28/17.
 */
public class Message {
    public String userId;
    public String sender;
    public String message;
    public String imageId;
    public Long timeStamp;

    public Message() {

    }
    public Message(String userId, String name, String message, String imageId, Long timeStamp) {
        this.userId = userId;
        this.sender = name;
        this.message = message;
        this.imageId = imageId;
        this.timeStamp = timeStamp;
    }
}
