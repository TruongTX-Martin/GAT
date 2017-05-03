package com.gat.feature.bookdetailborrow;

import com.gat.app.screen.Screen;
import com.google.auto.value.AutoValue;

/**
 * Created by root on 26/04/2017.
 */

@AutoValue
public abstract class BookDetailBorrowScreen implements Screen{
    public static BookDetailBorrowScreen instance() {
        return new AutoValue_BookDetailBorrowScreen();
    }
}
