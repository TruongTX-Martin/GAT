package com.gat.feature.bookdetailrequest;

import com.gat.app.screen.Screen;
import com.google.auto.value.AutoValue;

/**
 * Created by root on 23/04/2017.
 */

@AutoValue
public abstract class BookDetailRequestScreen implements Screen {
    public static BookDetailRequestScreen instance() {
        return new AutoValue_BookDetailRequestScreen();
    }
}
