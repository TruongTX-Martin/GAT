package com.gat.feature.register.update.category;

import com.gat.app.screen.Screen;
import com.google.auto.value.AutoValue;

/**
 * Created by ducbtsn on 3/19/17.
 */

@AutoValue
public abstract class AddCategoryScreen implements Screen{
    public static AddCategoryScreen instance() {
        return new AutoValue_AddCategoryScreen();
    }
}
