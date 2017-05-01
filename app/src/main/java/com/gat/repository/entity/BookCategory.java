package com.gat.repository.entity;

import com.google.auto.value.AutoValue;

/**
 * Created by ducbtsn on 3/5/17.
 */

@AutoValue
public abstract class BookCategory {
    public abstract String name();
    public abstract int coverId();
    public abstract boolean favor();
    public abstract int categoryId();
    public static BookCategory instance(String name, int coverId, boolean favor, int categoryId) {
        return new AutoValue_BookCategory(name,coverId, favor, categoryId);
    }
}
