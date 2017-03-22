package com.gat.data.user;

import com.gat.repository.entity.LoginData;
import com.google.auto.value.AutoValue;

import java.io.Serializable;

/**
 * Created by Rey on 2/23/2017.
 */
@AutoValue
public abstract class SocialLoginData implements LoginData{

    public static SocialLoginData instance(String socialID, int type, String email, String password, String name, String image){
        return new AutoValue_SocialLoginData(socialID, type, email, password, name, image);
    }
    public abstract String socialID();
    public abstract int type();
    public abstract String email();
    public abstract String password();
    public abstract String name();
    public abstract String image();
}
