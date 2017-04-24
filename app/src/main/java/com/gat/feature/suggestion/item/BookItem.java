package com.gat.feature.suggestion.item;

import com.gat.common.adapter.Item;
import com.gat.repository.entity.Book;
import com.google.auto.value.AutoValue;

/**
 * Created by mozaa on 30/03/2017.
 */
@AutoValue
public abstract class BookItem implements Item {
    public static BookItem instance(Book book, String ratingText){
        return new AutoValue_BookItem(book,ratingText);
    }

    public abstract Book book();
    public abstract String ratingText();
}
