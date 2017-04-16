package com.gat.feature.book_detail;

import com.gat.app.screen.Screen;
import com.google.auto.value.AutoValue;

/**
 * Created by mryit on 4/16/2017.
 */

@AutoValue
public abstract class BookDetailScreen implements Screen {

    public static BookDetailScreen instance () {
        return new AutoValue_BookDetailScreen();
    }

}
