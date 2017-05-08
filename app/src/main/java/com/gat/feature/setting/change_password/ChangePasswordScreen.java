package com.gat.feature.setting.change_password;

import com.gat.app.screen.Screen;
import com.google.auto.value.AutoValue;

/**
 * Created by mozaa on 05/05/2017.
 */

@AutoValue
public abstract class ChangePasswordScreen implements Screen{

    public static ChangePasswordScreen instance () {
        return new AutoValue_ChangePasswordScreen();
    }

}
