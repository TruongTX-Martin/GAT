package com.gat.repository.entity;

import com.gat.common.util.Strings;
import com.google.auto.value.AutoValue;

/**
 * Created by mozaa on 01/04/2017.
 */
@AutoValue
public abstract class UserNearByDistance {

    public abstract long userId();
    public abstract String name();
    public abstract String imageId();
    public abstract String address();
    public abstract byte locationType();
    public abstract float latitude();
    public abstract float longitude();
    public abstract long distance();

//    public static Builder builder () {
//        return new AutoValue_UserNearByDistance.Builder().address(Strings.EMPTY);
//    }



//    @AutoValue.Builder
//    public abstract static class Builder {
//
//        public abstract Builder userId(long value);
//        public abstract Builder name(String value);
//        public abstract Builder imageId(String value);
//        public abstract Builder address(String value);
//        public abstract Builder locationType(byte value);
//        public abstract Builder latitude(float value);
//        public abstract Builder longitude(float value);
//        public abstract Builder distance(long value);
//        public abstract Book build();
//    }

}
