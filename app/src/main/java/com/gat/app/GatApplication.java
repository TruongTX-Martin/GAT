package com.gat.app;

import com.gat.common.BootstrapApplication;
import com.gat.common.listener.OnNetworkChangeListener;
import com.gat.dependency.AppComponent;
import com.gat.dependency.AppModule;
import com.gat.dependency.DaggerAppComponent;
import com.gat.feature.book_detail.BookDetailActivity;
import com.gat.feature.book_detail.comment.CommentActivity;

import io.paperdb.Paper;

/**
 * Created by Rey on 2/14/2017.
 */

public class GatApplication extends BootstrapApplication {

    OnNetworkChangeListener observerNetworkChange;

    @Override
    public void onCreate() {
        super.onCreate();
        Paper.init(this);

        observerNetworkChange = new OnNetworkChangeListener();

    }

    @Override
    protected AppComponent prepareAppComponent() {
        return DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .build();
    }

    public OnNetworkChangeListener getObserverNetworkChange() {
        return observerNetworkChange;
    }
}
