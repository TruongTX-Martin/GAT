package com.gat.repository.entity;

import com.gat.common.util.Strings;
import com.gat.data.id.LongId;
import com.google.auto.value.AutoValue;

/**
 * Created by Rey on 2/23/2017.
 */
@AutoValue
public abstract class   User {

    public static User NONE = builder().id(Id.NONE).name(Strings.EMPTY).build();

    public abstract Id id();
    public abstract String name();
    public abstract String avatar();
    public abstract String address();
    public abstract String phoneNumber();
    public abstract String imageId();

    public static Builder builder(){
        return new AutoValue_User.Builder()
                .name("Test")
                .avatar(Strings.EMPTY)
                .address(Strings.EMPTY)
                .imageId(Strings.EMPTY)
                .phoneNumber(Strings.EMPTY);
    }

    @AutoValue.Builder
    public abstract static class Builder {

        public abstract Builder id(Id value);
        public abstract Builder name(String value);
        public abstract Builder avatar(String value);
        public abstract Builder address(String value);
        public abstract Builder phoneNumber(String value);
        public abstract Builder imageId(String imageId);
        public abstract User build();

    }
}
