package com.gat.feature.scanbarcode;

import com.gat.app.screen.Screen;
import com.google.auto.value.AutoValue;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by ducbtsn on 4/24/17.
 */
@AutoValue
public abstract class ScanScreen implements Screen {
    @Retention(RetentionPolicy.SOURCE)
    public @interface From {
        int SEARCH        = 0;
        int TAB           = 1;
    }

    public abstract int from();
    public static ScanScreen instance(int from) {
        return new AutoValue_ScanScreen(from);
    }

    public static ScanScreen FROM_SEARCH = new AutoValue_ScanScreen(From.SEARCH);
    public static ScanScreen FROM_TAB = new AutoValue_ScanScreen(From.TAB);
}

