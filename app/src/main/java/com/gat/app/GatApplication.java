package com.gat.app;

import com.gat.common.BootstrapApplication;
import com.gat.dependency.AppComponent;
import com.gat.dependency.AppModule;
import com.gat.dependency.DaggerAppComponent;

import io.paperdb.Paper;

/**
 * Created by Rey on 2/14/2017.
 */

public class GatApplication extends BootstrapApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        Paper.init(this);
    }

    @Override
    protected AppComponent prepareAppComponent() {
        return DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .build();
    }
}
