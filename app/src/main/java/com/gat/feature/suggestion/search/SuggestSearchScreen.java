package com.gat.feature.suggestion.search;

import com.gat.app.screen.Screen;
import com.google.auto.value.AutoValue;

/**
 * Created by mryit on 4/9/2017.
 */

@AutoValue
public abstract class SuggestSearchScreen implements Screen {
    public static SuggestSearchScreen instance () {
        return new AutoValue_SuggestSearchScreen();
    }
}
