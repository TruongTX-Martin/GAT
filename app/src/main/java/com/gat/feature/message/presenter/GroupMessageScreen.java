package com.gat.feature.message.presenter;

import com.gat.app.screen.Screen;
import com.google.auto.value.AutoValue;

/**
 * Created by ducbtsn on 4/24/17.
 */
@AutoValue
public abstract class GroupMessageScreen implements Screen{
    public static GroupMessageScreen instance() {
        return new AutoValue_GroupMessageScreen();
    }
}
