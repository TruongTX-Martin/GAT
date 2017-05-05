package com.gat.feature.setting;

import com.gat.app.screen.Screen;
import com.google.auto.value.AutoValue;

/**
 * Created by mryit on 5/5/2017.
 */

@AutoValue
public abstract class SettingScreen implements Screen{

    public static SettingScreen instance () {
        return new AutoValue_SettingScreen();
    }

}
