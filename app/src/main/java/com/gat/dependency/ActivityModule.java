package com.gat.dependency;


import android.app.Activity;

import dagger.Module;

/**
 * Created by Rey on 5/10/2016.
 */
@Module
public class ActivityModule {

    private Activity activity;

    public ActivityModule(Activity activity){
        this.activity = activity;
    }

}
