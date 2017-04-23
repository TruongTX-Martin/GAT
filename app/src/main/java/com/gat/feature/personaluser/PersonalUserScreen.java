package com.gat.feature.personaluser;

import com.gat.app.screen.Screen;
import com.google.auto.value.AutoValue;

/**
 * Created by root on 20/04/2017.
 */

@AutoValue
public abstract class PersonalUserScreen implements Screen{
    public static PersonalUserScreen instance() {
        return new AutoValue_PersonalUserScreen();
    }
}
