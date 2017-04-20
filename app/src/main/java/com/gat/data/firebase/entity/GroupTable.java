package com.gat.data.firebase.entity;

import java.util.List;

/**
 * Created by ducbtsn on 4/18/17.
 */

public class GroupTable {
    private List<Long> users;
    private String name;
    private long timeStamp;

    public List<Long> getUsers() {
        return this.users;
    }

    public String getName() {
        return this.name;
    }

    public Long getTimeStamp() {
        return this.timeStamp;
    }

    public void setUsers(List<Long> users) {
        this.users = users;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }
}
