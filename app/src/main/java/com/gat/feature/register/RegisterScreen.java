package com.gat.feature.register;

import com.gat.app.screen.Screen;
import com.gat.domain.usecase.Register;
import com.google.auto.value.AutoValue;

/**
 * Created by ducbtsn on 2/26/17.
 */

@AutoValue
public abstract class RegisterScreen implements Screen {
    public static RegisterScreen instance() {
        return new AutoValue_RegisterScreen();
    }
}
