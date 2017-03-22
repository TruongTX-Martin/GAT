package com.gat.app;

import com.gat.R;
import com.gat.common.BootstrapApplication;

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
}
