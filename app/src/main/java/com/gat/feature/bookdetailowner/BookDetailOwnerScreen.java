package com.gat.feature.bookdetailowner;

import com.gat.app.screen.Screen;
import com.google.auto.value.AutoValue;

/**
 * Created by root on 26/04/2017.
 */

@AutoValue
public abstract class BookDetailOwnerScreen implements Screen{
    public abstract int requestId();
    public static BookDetailOwnerScreen instance(int requestId) {
        return new AutoValue_BookDetailOwnerScreen(requestId);
    }
}
