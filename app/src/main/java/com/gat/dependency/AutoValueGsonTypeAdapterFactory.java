package com.gat.dependency;

import com.gat.data.response.impl.NotifyEntity;
import com.gat.repository.entity.User;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;

/**
 * Created by mozaa on 28/04/2017.
 */

public class AutoValueGsonTypeAdapterFactory implements TypeAdapterFactory {


    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        Class<? super T> rawType = type.getRawType();

        if (rawType.equals(NotifyEntity.class)) {
            return (TypeAdapter<T>) NotifyEntity.typeAdapter(gson);
        } else if (rawType.equals(User.class)) {
            return (TypeAdapter<T>) User.typeAdapter(gson);
        }

        return null;
    }

}
