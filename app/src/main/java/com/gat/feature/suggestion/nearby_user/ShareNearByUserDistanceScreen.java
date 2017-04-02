package com.gat.feature.suggestion.nearby_user;

import android.support.annotation.Nullable;

import com.gat.app.screen.Screen;
import com.gat.repository.entity.UserNearByDistance;
import com.google.auto.value.AutoValue;

import java.util.List;

/**
 * Created by mozaa on 02/04/2017.
 */

@AutoValue
public abstract class ShareNearByUserDistanceScreen implements Screen {
    public static ShareNearByUserDistanceScreen instance() {
        return new AutoValue_ShareNearByUserDistanceScreen();
    }

}
