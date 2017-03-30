package com.gat.dependency;

import android.app.Application;

import com.gat.app.screen.ScreenPresenterFactory;
import com.gat.data.response.ServerResponse;
import com.gat.data.user.PaperUserDataSource;
import com.gat.domain.SchedulerFactory;
import com.gat.domain.UseCaseFactory;
import com.gat.domain.impl.SchedulerFactoryImpl;
import com.gat.domain.impl.UseCaseFactoryImpl;
import com.gat.repository.BookRepository;
import com.gat.repository.MessageRepository;
import com.gat.repository.UserRepository;
import com.gat.repository.datasource.BookDataSource;
import com.gat.repository.datasource.MessageDataSource;
import com.gat.repository.datasource.UserDataSource;
import com.gat.repository.impl.BookRepositoryImpl;
import com.gat.repository.impl.MesssageRepositoryImpl;
import com.gat.repository.impl.UserRepositoryImpl;
import com.rey.mvp2.PresenterManager;
import com.rey.mvp2.impl.SimplePresenterManager;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Lazy;
import dagger.Module;
import dagger.Provides;

/**
 * Created by Rey on 12/7/2015.
 */
@Module
public class AppModule {
    private final String BASE_URL = "http://gatbook-api-v1.azurewebsites.net/api/";
    private final String LANG_EN = "en";

    private Application application;

    public AppModule(Application application){
        this.application = application;
    }

    @Provides
    @Singleton
    Application provideApplication(){
        return application;
    }

    @Provides
    @Singleton
    @Named("network")
    BookDataSource provideNetworkBookDataSource(DataComponent dataComponent){
        //TODO: provide implementation
        return null;
    }

    @Provides
    @Singleton
    @Named("local")
    BookDataSource provideLocalBookDataSource(){
        //TODO: provide implementation
        return null;
    }

    @Provides
    @Singleton
    BookRepository provideBookRepository(@Named("network")Lazy<BookDataSource> networkDataSourceLazy,
                                         @Named("local")Lazy<BookDataSource> localDataSourceLazy){
        return new BookRepositoryImpl(networkDataSourceLazy, localDataSourceLazy);
    }

    @Provides
    @Singleton
    @Named("network")
    UserDataSource provideNetworkUserDataSource(DataComponent dataComponent){
        //TODO: provide implementation
        return null;
    }

    @Provides
    @Singleton
    @Named("local")
    UserDataSource provideLocalUserDataSource(){
        return new PaperUserDataSource();
    }

    @Provides
    @Singleton
    @Named("network")
    MessageDataSource provideNetworkMessageDataSource() {
        // TODO: provide implementation
        return null;
    }

    @Provides
    @Singleton
    @Named("local")
    MessageDataSource provideLocalMessageDataSource() {
        // TODO: provide implementation
        return null;
    }

    @Provides
    @Singleton
    MessageRepository provideMessageRepository(@Named("network")Lazy<MessageDataSource> networkDataSourceLazy,
                                         @Named("local")Lazy<MessageDataSource> localDataSourceLazy){
        return new MesssageRepositoryImpl(networkDataSourceLazy, localDataSourceLazy);
    }

    @Provides
    @Singleton
    UserRepository provideUserRepository(@Named("network")Lazy<UserDataSource> networkDataSourceLazy,
                                         @Named("local")Lazy<UserDataSource> localDataSourceLazy){
        return new UserRepositoryImpl(networkDataSourceLazy, localDataSourceLazy);
    }



    @Provides
    @Singleton
    SchedulerFactory provideSchedulerFactory(){
        return new SchedulerFactoryImpl();
    }

    @Provides
    @Singleton
    UseCaseFactory provideUseCaseFactory(Lazy<BookRepository> bookRepositoryLazy,
                                         Lazy<UserRepository> userRepositoryLazy,
                                         Lazy<MessageRepository> messageRepositoryLazy){
        return new UseCaseFactoryImpl(bookRepositoryLazy, userRepositoryLazy, messageRepositoryLazy);
    }

    @Provides
    @Singleton
    PresenterManager providerPresenterManager(){
        PresenterComponent presenterComponent = Components.getComponent(application, AppComponent.class)
                .plus(new PresenterModule());
        return new SimplePresenterManager(new ScreenPresenterFactory(presenterComponent));
    }

    @Provides
    @Singleton
    DataComponent provideDataComponent(@Named("local") Lazy<UserDataSource> localUserDataSourceLazy) {
        return DaggerDataComponent.builder().dataModule(new DataModule(BASE_URL, LANG_EN, localUserDataSourceLazy.get())).build();
    }
}
