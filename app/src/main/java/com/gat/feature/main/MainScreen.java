package com.gat.feature.main;

import com.gat.app.screen.Screen;
import com.google.auto.value.AutoValue;

/**
 * Created by mryit on 3/26/2017.
 */

@AutoValue
public abstract class MainScreen implements Screen {
    public static MainScreen instance() {
    return new AutoValue_MainScreen();
}
}
