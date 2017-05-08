package com.gat.feature.setting.main;

import com.gat.app.screen.Screen;
import com.google.auto.value.AutoValue;

/**
 * Created by mryit on 5/5/2017.
 */

@AutoValue
public abstract class MainSettingScreen implements Screen {

    public static MainSettingScreen instance () {
        return new AutoValue_MainSettingScreen();
    }

}
