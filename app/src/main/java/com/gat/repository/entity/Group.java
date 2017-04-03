package com.gat.repository.entity;

import com.gat.common.util.Strings;
import com.google.auto.value.AutoValue;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by ducbtsn on 4/1/17.
 */

@AutoValue
public abstract class Group {
    public abstract String groupId();
    public abstract String lastMessage();
    public abstract Long timeStamp();
    public abstract List<String> users();

    public static Group instance(Group group) {
        return new AutoValue_Group.Builder()
                .groupId(group.groupId())
                .lastMessage(group.lastMessage())
                .users(group.users())
                .timeStamp(group.timeStamp())
                .build();
    }

    public static Builder builder() {
        return new AutoValue_Group.Builder()
                .groupId(Strings.EMPTY)
                .lastMessage(Strings.EMPTY)
                .timeStamp(new Date().getTime())
                .users(new ArrayList<String>());
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder groupId(String groupId);
        public abstract Builder lastMessage(String message);
        public abstract Builder timeStamp(Long timeStamp);
        public abstract Builder users(List<String> users);
        public abstract Group build();
    }

}
