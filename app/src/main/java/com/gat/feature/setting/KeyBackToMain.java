package com.gat.feature.setting;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by mryit on 5/7/2017.
 */

@Retention(RetentionPolicy.SOURCE)
public @interface KeyBackToMain {

    int DO_NOTHING = -1;
    int BACK_BUTTON = 0;
    int BACK_CHANGE_PASSWORD = 1;
    int ADD_EMAIL_PASSWORD = 99;
    int DISCONNECT_FACEBOOK = 9;
    int DISCONNECT_TWITTER = 8;
    int DISCONNECT_GOOGLE = 7;
}
