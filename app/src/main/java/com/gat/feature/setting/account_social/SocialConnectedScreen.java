package com.gat.feature.setting.account_social;

import com.gat.app.screen.Screen;
import com.google.auto.value.AutoValue;

/**
 * Created by mozaa on 05/05/2017.
 */

@AutoValue
public abstract class SocialConnectedScreen implements Screen {

    public static SocialConnectedScreen instance () {
        return new AutoValue_SocialConnectedScreen();
    }

}
