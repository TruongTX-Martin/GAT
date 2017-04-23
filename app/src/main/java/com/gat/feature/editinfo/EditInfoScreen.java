package com.gat.feature.editinfo;

import com.gat.app.screen.Screen;
import com.google.auto.value.AutoValue;

/**
 * Created by root on 18/04/2017.
 */
@AutoValue
public abstract class EditInfoScreen implements Screen{
    public static EditInfoScreen instance() {
        return new AutoValue_EditInfoScreen();
    }

}
