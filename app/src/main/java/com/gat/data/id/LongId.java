package com.gat.data.id;

import com.gat.repository.entity.Id;
import com.google.auto.value.AutoValue;

/**
 * Created by Rey on 2/14/2017.
 */
@AutoValue
public abstract class LongId implements Id {

    public static LongId instance(long value){
        return new AutoValue_LongId(value);
    }

    public abstract long value();

}
