package com.gat.feature.setting.add_email_password;

import com.gat.app.screen.Screen;
import com.google.auto.value.AutoValue;

/**
 * Created by mryit on 5/5/2017.
 */

@AutoValue
public abstract class AddEmailPasswordScreen implements Screen {

    public static AddEmailPasswordScreen instance () {
        return new AutoValue_AddEmailPasswordScreen();
    }

}
