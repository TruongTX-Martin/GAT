package com.gat.feature.search.item;

import com.gat.common.adapter.Item;
import com.gat.repository.entity.Book;
import com.google.auto.value.AutoValue;

/**
 * Created by Rey on 2/15/2017.
 */
@AutoValue
public abstract class BookItem implements Item {

    public static BookItem instance(Book book, String authorNames, String ratingText){
        return new AutoValue_BookItem(book, authorNames, ratingText);
    }

    public abstract Book book();
    public abstract String authorNames();
    public abstract String ratingText();
}
