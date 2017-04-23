package com.gat.app;

import com.gat.dependency.AppComponent;
import com.gat.dependency.DaggerAppComponent;
import com.gat.dependency.DebugAppModule;

/**
 * Created by Rey on 2/14/2017.
 */

public class DebugGatApplication extends GatApplication {

    @Override
    protected AppComponent prepareAppComponent() {
        return DaggerAppComponent.builder()
                .appModule(new DebugAppModule(this))
                .build();
    }
}
