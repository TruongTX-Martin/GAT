package com.gat.feature.bookdetailsender;

import com.gat.app.screen.Screen;
import com.google.auto.value.AutoValue;

/**
 * Created by root on 23/04/2017.
 */

@AutoValue
public abstract class BookDetailSenderScreen implements Screen {
    public abstract int requestId();
    public static BookDetailSenderScreen instance(int requestId) {
        return new AutoValue_BookDetailSenderScreen(requestId);
    }
}
