package com.gat.dependency;

import android.app.Application;

import com.gat.data.DebugBookDataSource;
import com.gat.data.DebugUserDataSource;
import com.gat.data.user.PaperUserDataSource;
import com.gat.repository.datasource.BookDataSource;
import com.gat.repository.datasource.UserDataSource;

import javax.inject.Named;

import dagger.Lazy;

/**
 * Created by Rey on 2/14/2017.
 */

public class DebugAppModule extends AppModule {

    public DebugAppModule(Application application) {
        super(application);
    }

    @Override
    BookDataSource provideNetworkBookDataSource() {
        return new DebugBookDataSource();
    }

    @Override
    BookDataSource provideLocalBookDataSource() {
        return new DebugBookDataSource();
    }

    @Override
    UserDataSource provideNetworkUserDataSource(DataComponent dataComponent) {
        return new DebugUserDataSource(dataComponent);
    }
}
