package com.gat.feature.search;

import android.support.annotation.Nullable;

import com.gat.app.screen.Screen;
import com.google.auto.value.AutoValue;

/**
 * Created by Rey on 2/14/2017.
 */
@AutoValue
public abstract class SearchScreen implements Screen {

    public static SearchScreen instance(@Nullable String keyword){
        return new AutoValue_SearchScreen(keyword);
    }

    public abstract @Nullable String keyword();
}
