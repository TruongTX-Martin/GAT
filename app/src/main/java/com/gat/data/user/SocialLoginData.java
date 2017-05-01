package com.gat.data.user;

import android.support.annotation.Nullable;

import com.gat.common.util.Strings;
import com.gat.repository.entity.LoginData;
import com.google.auto.value.AutoValue;

import java.io.Serializable;

/**
 * Created by Rey on 2/23/2017.
 */
@AutoValue
public abstract class SocialLoginData implements LoginData{

    public static SocialLoginData instance(String socialID, int type, String email, String password, String name, String image, String token){
        return new AutoValue_SocialLoginData(socialID, type, email, password, name, image, token, Strings.EMPTY);
    }

    public static SocialLoginData instance(String socialID, int type, String email, String password, String name, String image, String token, String secret){
        return new AutoValue_SocialLoginData(socialID, type, email, password, name, image, token, secret);
    }
    public abstract String socialID();
    public abstract int type();
    public abstract String email();
    public abstract String password();
    public abstract String name();
    public abstract String image();
    public abstract String token();
    public abstract @Nullable String secret();
}
