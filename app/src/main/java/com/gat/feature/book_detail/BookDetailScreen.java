package com.gat.feature.book_detail;

import com.gat.app.screen.Screen;
import com.google.auto.value.AutoValue;

/**
 * Created by mryit on 4/16/2017.
 */

@AutoValue
public abstract class BookDetailScreen implements Screen {

    public static BookDetailScreen instance (int editionId) {
        return new AutoValue_BookDetailScreen(editionId);
    }

    public abstract int editionId();

}
