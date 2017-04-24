package com.gat.dependency;

import android.app.Application;

import com.gat.data.DebugBookDataSource;
import com.gat.data.DebugLocalMessageDataSource;
import com.gat.data.DebugMessageDataSource;
import com.gat.data.DebugUserDataSource;
import com.gat.data.firebase.FirebaseService;
import com.gat.data.user.PaperUserDataSource;
import com.gat.repository.MessageRepository;
import com.gat.repository.datasource.BookDataSource;
import com.gat.repository.datasource.MessageDataSource;
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
    BookDataSource provideNetworkBookDataSource(DataComponent dataComponent) {
        return new DebugBookDataSource(dataComponent);
    }

    @Override
    UserDataSource provideNetworkUserDataSource(DataComponent dataComponent) {
        return new DebugUserDataSource(dataComponent);
    }

    @Override
    MessageDataSource provideNetworkMessageDataSource(FirebaseService firebaseService) {
        return new DebugMessageDataSource(firebaseService);
    }

    @Override
    MessageDataSource provideLocalMessageDataSource() {
        return new DebugLocalMessageDataSource();
    }
    
}
