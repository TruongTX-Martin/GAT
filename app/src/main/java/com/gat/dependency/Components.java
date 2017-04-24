package com.gat.dependency;

import android.content.Context;

/**
 * Created by Rey on 6/17/2016.
 */
public class Components {

    public static <T extends Object> T getComponent(Context context, Class<T> clazz){
        if(!(context instanceof HasComponent))
            throw new IllegalArgumentException("context must be instance of HasComponent.");

        Object obj = ((HasComponent)context).getComponent();

        if(!clazz.isAssignableFrom(obj.getClass()))
            throw new ClassCastException(obj.getClass() + " cannot be cast to " + clazz);

        return (T) obj;
    }
}
