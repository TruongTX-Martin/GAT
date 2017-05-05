package com.gat.data.user;

import com.gat.repository.entity.LoginData;
import com.google.auto.value.AutoValue;

/**
 * Created by Rey on 2/23/2017.
 */
@AutoValue
public abstract class EmailLoginData implements LoginData {

    public static EmailLoginData instance(String email, String password, String name, String image, int mode){
        return new AutoValue_EmailLoginData(email, password, name, image, mode, "");
    }

    public static EmailLoginData instance(String email, String password, String name, String image, int mode, String firebasePassword){
        return new AutoValue_EmailLoginData(email, password, name, image, mode, firebasePassword);
    }
    public abstract String email();
    public abstract String password();
    public abstract String name();
    public abstract String image();
    public abstract int type();
    public abstract String firebasePassword();
}
