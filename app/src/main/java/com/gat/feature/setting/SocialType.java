package com.gat.feature.setting;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by mozaa on 09/05/2017.
 */
// 1=Facebook, 2=Google, 3=Twitter
@Retention(RetentionPolicy.SOURCE)
public @interface SocialType {
    int FACEBOOK = 1;
    int GOOGLE = 2;
    int TWITTER = 3;
}
