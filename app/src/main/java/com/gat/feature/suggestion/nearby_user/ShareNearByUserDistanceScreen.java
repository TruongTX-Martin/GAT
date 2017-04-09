package com.gat.feature.suggestion.nearby_user;

import com.gat.app.screen.Screen;
import com.google.auto.value.AutoValue;

/**
 * Created by mozaa on 02/04/2017.
 */

@AutoValue
public abstract class ShareNearByUserDistanceScreen implements Screen {
    public static ShareNearByUserDistanceScreen instance() {
        return new AutoValue_ShareNearByUserDistanceScreen();
    }

}
