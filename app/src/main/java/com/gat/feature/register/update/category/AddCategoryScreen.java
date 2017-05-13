package com.gat.feature.register.update.category;

import com.gat.app.screen.Screen;
import com.gat.repository.entity.InterestCategory;
import com.google.auto.value.AutoValue;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ducbtsn on 3/19/17.
 */

@AutoValue
public abstract class AddCategoryScreen implements Screen{
    public abstract List<InterestCategory> categories();
    public static AddCategoryScreen instance() {
        return new AutoValue_AddCategoryScreen(new ArrayList<>());
    }

    public static AddCategoryScreen instance(List<InterestCategory> categories) {
        return new AutoValue_AddCategoryScreen(categories);
    }
}
