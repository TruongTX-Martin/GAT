package com.gat.feature.book_detail.self_update_reading;

import android.support.annotation.IntDef;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by mozaa on 21/04/2017.
 */

@IntDef({ReadingState.REMOVE, ReadingState.RED, ReadingState.READING, ReadingState.TO_READ})
@Retention(RetentionPolicy.SOURCE)
public @interface ReadingState {
    int REMOVE  = -1;
    int RED     = 0;
    int READING = 1;
    int TO_READ = 2;
}
