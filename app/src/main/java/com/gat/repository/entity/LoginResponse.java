package com.gat.repository.entity;

import com.gat.common.util.Strings;
import com.google.auto.value.AutoValue;

/**
 * Created by ducbtsn on 2/25/17.
 */

@AutoValue
public abstract class LoginResponse {
    public abstract int httpStatus();
    public abstract String message();
    public abstract Id id();
    public abstract String name();
    public abstract String avatar();
    public abstract String address();
    public abstract String phoneNumber();
    public abstract String token();

    public static Builder builder(){
        return new AutoValue_LoginResponse.Builder()
                .avatar(Strings.EMPTY)
                .address(Strings.EMPTY)
                .phoneNumber(Strings.EMPTY);
    }

    @AutoValue.Builder
    public abstract static class Builder {

        public abstract Builder id(Id value);
        public abstract Builder name(String value);
        public abstract Builder avatar(String value);
        public abstract Builder address(String value);
        public abstract Builder phoneNumber(String value);
        public abstract Builder httpStatus(int status);
        public abstract Builder message(String message);
        public abstract Builder token(String token);
        public abstract LoginResponse build();

    }
}
