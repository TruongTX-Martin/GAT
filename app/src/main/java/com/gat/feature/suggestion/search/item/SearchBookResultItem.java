package com.gat.feature.suggestion.search.item;

import com.gat.common.adapter.Item;
import com.gat.data.response.BookResponse;
import com.google.auto.value.AutoValue;

/**
 * Created by mryit on 4/15/2017.
 */

@AutoValue
public abstract class SearchBookResultItem implements Item {

    public static SearchBookResultItem instance (BookResponse bookResponse) {
        return new AutoValue_SearchBookResultItem(bookResponse);
    }

    public abstract BookResponse bookResponse();

}
