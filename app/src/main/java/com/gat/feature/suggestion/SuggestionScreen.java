package com.gat.feature.suggestion;

import com.gat.app.screen.Screen;
import com.google.auto.value.AutoValue;

/**
 * Created by ducbtsn on 3/25/17.
 */

@AutoValue
public abstract class SuggestionScreen implements Screen{
    public static SuggestionScreen instance() {
        return new AutoValue_SuggestionScreen();
    }
}
