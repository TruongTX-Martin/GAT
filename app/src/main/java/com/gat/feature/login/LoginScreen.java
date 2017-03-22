package com.gat.feature.login;

import android.support.annotation.Nullable;

import com.gat.app.screen.Screen;
import com.google.auto.value.AutoValue;

/**
 * Created by ducbtsn on 2/23/17.
 */

@AutoValue
public abstract class LoginScreen implements Screen {
    public abstract @Nullable String email();
    public static LoginScreen instance(@Nullable String email) {
        return new AutoValue_LoginScreen(email);
    }
}
