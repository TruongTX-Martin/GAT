package com.gat.data.firebase.entity;

import com.gat.common.util.Strings;
import com.google.auto.value.AutoValue;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by ducbtsn on 4/18/17.
 */
@AutoValue
public abstract class GroupTable {
    public abstract String groupId();
    public abstract String lastMessage();
    public abstract Long timeStamp();
    public abstract Boolean isRead();
    public abstract List<String> users();

    public static GroupTable instance(GroupTable groupTable) {
        return new AutoValue_GroupTable.Builder()
                .groupId(groupTable.groupId())
                .lastMessage(groupTable.lastMessage())
                .users(groupTable.users())
                .isRead(groupTable.isRead())
                .timeStamp(groupTable.timeStamp())
                .build();
    }

    public static GroupTable.Builder builder() {
        return new AutoValue_GroupTable.Builder()
                .groupId(Strings.EMPTY)
                .lastMessage(Strings.EMPTY)
                .timeStamp(new Date().getTime())
                .isRead(false)
                .users(new ArrayList<String>());
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder groupId(String groupId);
        public abstract Builder lastMessage(String message);
        public abstract Builder timeStamp(Long timeStamp);
        public abstract Builder isRead(Boolean isRead);
        public abstract Builder users(List<String> users);
        public abstract GroupTable build();
    }

}
