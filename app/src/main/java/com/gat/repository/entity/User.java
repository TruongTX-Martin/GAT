package com.gat.repository.entity;

import com.gat.common.util.Strings;
import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;

/**
 * Created by Rey on 2/23/2017.
 */
@AutoValue
public abstract class User {

    public static int INVALID_USERID = 0;
    public static User NONE = builder().userId(INVALID_USERID).name(Strings.EMPTY).build();
    public boolean isValid() {
        return userId() != INVALID_USERID;
    }
    public static TypeAdapter<User> typeAdapter(Gson gson) {
        return new AutoValue_User.GsonTypeAdapter(gson);
    }
    public abstract int userId();
    public abstract String name();
    public abstract String email();
    public abstract String imageId();
    public abstract int userTypeFlag();
    public abstract int deleteFlag();
    public abstract int loanCount();
    public abstract int readCount();
    public abstract int requestCount();

    public static Builder builder(){
        return new AutoValue_User.Builder()
                .email(Strings.EMPTY)
                .imageId(Strings.EMPTY)
                .userTypeFlag(0)
                .deleteFlag(0)
                .requestCount(0)
                .loanCount(0)
                .readCount(0);
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
        public abstract User build();

    }
}
