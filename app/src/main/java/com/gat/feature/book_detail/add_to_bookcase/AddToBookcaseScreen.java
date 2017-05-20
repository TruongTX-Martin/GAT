package com.gat.feature.book_detail.add_to_bookcase;

import com.gat.app.screen.Screen;
import com.gat.data.response.impl.BookInfo;
import com.google.auto.value.AutoValue;

/**
 * Created by mryit on 4/22/2017.
 */

@AutoValue
public abstract class AddToBookcaseScreen implements Screen {

    public static AddToBookcaseScreen instance (BookInfo bookInfo, String authorName){
        return new AutoValue_AddToBookcaseScreen(bookInfo, authorName);
    }

    public abstract BookInfo bookInfo ();

    public abstract String authorName ();

}
