package com.gat.feature.book_detail.self_update_reading;

import com.gat.app.screen.Screen;
import com.gat.data.response.impl.BookReadingInfo;
import com.google.auto.value.AutoValue;

/**
 * Created by mryit on 4/20/2017.
 */

@AutoValue
public abstract class SelfUpdateReadingScreen implements Screen {

    public static SelfUpdateReadingScreen instance (BookReadingInfo bookReadingInfo) {
        return new AutoValue_SelfUpdateReadingScreen(bookReadingInfo);
    }

    public abstract BookReadingInfo bookReadingInfo();

}
