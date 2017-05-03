package com.gat.feature.bookdetailowner;

import com.gat.app.screen.Screen;
import com.google.auto.value.AutoValue;

/**
 * Created by root on 26/04/2017.
 */

@AutoValue
public abstract class BookDetailOwnerScreen implements Screen{
    public static BookDetailOwnerScreen instance() {
        return new AutoValue_BookDetailOwnerScreen();
    }
}
