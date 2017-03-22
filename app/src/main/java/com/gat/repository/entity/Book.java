package com.gat.repository.entity;

import android.support.annotation.Nullable;

import com.google.auto.value.AutoValue;

import java.util.Collections;
import java.util.List;

/**
 * Created by Rey on 2/13/2017.
 */
@AutoValue
public abstract class Book {

    public abstract Id id();
    public abstract String title();
    public abstract @Nullable String description();
    public abstract String publisher();
    public abstract long publishedDate();
    public abstract int pages();
    public abstract float rating();
    public abstract List<Author> authors();
    public abstract List<Cover> covers();

    public static Builder builder(){
        return new AutoValue_Book.Builder()
                .authors(Collections.emptyList())
                .covers(Collections.emptyList());
    }

    @AutoValue.Builder
    public abstract static class Builder {

        public abstract Builder id(Id value);
        public abstract Builder title(String value);
        public abstract Builder description(String value);
        public abstract Builder publisher(String value);
        public abstract Builder publishedDate(long value);
        public abstract Builder pages(int value);
        public abstract Builder rating(float value);
        public abstract Builder authors(List<Author> value);
        public abstract Builder covers(List<Cover> value);
        public abstract Book build();

    }

}
