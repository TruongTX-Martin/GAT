package com.gat.feature.setting.account_social;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by mozaa on 05/05/2017.
 */

@Retention(RetentionPolicy.SOURCE)
public @interface TypeSocial {
    int FACEBOOK = 1;
    int GOOGLE = 2;
    int TWITTER = 3;
}
