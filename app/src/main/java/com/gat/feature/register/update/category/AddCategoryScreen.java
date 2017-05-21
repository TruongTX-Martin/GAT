package com.gat.feature.register.update.category;

import com.gat.app.screen.Screen;
import com.gat.repository.entity.InterestCategory;
import com.google.auto.value.AutoValue;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ducbtsn on 3/19/17.
 */

@AutoValue
public abstract class AddCategoryScreen implements Screen{
    @Retention(RetentionPolicy.SOURCE)
    public @interface FROM {
        int LOGIN        = 0;
        int EDIT_INFO    = 1;
    }
    public abstract List<InterestCategory> categories();
    public abstract int requestFrom();
    public static AddCategoryScreen instance() {
        return new AutoValue_AddCategoryScreen(new ArrayList<InterestCategory>(), FROM.LOGIN);
    }

    public static AddCategoryScreen instance(List<InterestCategory> categories, int from) {
        return new AutoValue_AddCategoryScreen(categories, from);
    }
}
