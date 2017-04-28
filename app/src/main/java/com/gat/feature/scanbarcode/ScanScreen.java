package com.gat.feature.scanbarcode;

import com.gat.app.screen.Screen;
import com.google.auto.value.AutoValue;

/**
 * Created by ducbtsn on 4/24/17.
 */
@AutoValue
public abstract class ScanScreen implements Screen {
    public static ScanScreen instance() {
        return new AutoValue_ScanScreen();
    }
}

