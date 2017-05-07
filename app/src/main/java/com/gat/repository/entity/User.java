package com.gat.repository.entity;

import android.support.annotation.Nullable;

import com.gat.common.util.Strings;
import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;

import org.json.JSONArray;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rey on 2/23/2017.
 */
@AutoValue
public abstract class User implements Serializable {

    public static int INVALID_USERID = 0;
    public static User NONE = builder().userId(INVALID_USERID).name(Strings.EMPTY)
//            .usuallyLocation(new ArrayList<>())
            .build();
    public static User DEFAULT = builder().userId(INVALID_USERID)       // TODO make default user
            .name("Invalid user")
            .email("")
            .imageId("33328625223")
//            .usuallyLocation(new ArrayList<>())
            .build();
    public boolean isValid() {
        return userId() != INVALID_USERID;
    }
    public static TypeAdapter<User> typeAdapter(Gson gson) {
        return new AutoValue_User.GsonTypeAdapter(gson);
    }
    public abstract int userId();
    public abstract String name();
    public abstract @Nullable String email();
    public abstract @Nullable String imageId();
    public abstract int userTypeFlag();
    public abstract int deleteFlag();
    public abstract int loanCount();
    public abstract int readCount();
    public abstract int requestCount();
    public abstract int passwordFlag();
    public abstract @Nullable String faceBookId();
    public abstract @Nullable String faceBookName();
    public abstract @Nullable String googleId();
    public abstract @Nullable String googleName();
    public abstract @Nullable String twitterId();
    public abstract @Nullable String twitterName();

//    public abstract List<UsuallyLocation> usuallyLocation();

    public static Builder builder(){
        return new AutoValue_User.Builder()
                .email(Strings.EMPTY)
                .imageId(Strings.EMPTY)
                .userTypeFlag(0)
                .deleteFlag(0)
                .requestCount(0)
                .loanCount(0)
                .readCount(0)
                .passwordFlag(0)
                .faceBookId(Strings.EMPTY)
                .faceBookName(Strings.EMPTY)
                .googleId(Strings.EMPTY)
                .googleName(Strings.EMPTY)
                .twitterId(Strings.EMPTY)
                .twitterName(Strings.EMPTY);
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder userId(int userId);
        public abstract Builder name(String name);
        public abstract Builder email(String email);
        public abstract Builder imageId(String imageId);
        public abstract Builder userTypeFlag(int flag);
        public abstract Builder deleteFlag(int flag);
        public abstract Builder loanCount(int cnt);
        public abstract Builder readCount(int cnt);
        public abstract Builder requestCount(int cnt);
        public abstract Builder passwordFlag(int flag);
        public abstract Builder faceBookId(String faceBookId);
        public abstract Builder faceBookName(String faceBookName);
        public abstract Builder googleId(String googleId);
        public abstract Builder googleName(String googleName);
        public abstract Builder twitterId(String twitterId);
        public abstract Builder twitterName(String twitterName);
//        public abstract Builder usuallyLocation(List<UsuallyLocation> list);
        public abstract User build();
    }



}
