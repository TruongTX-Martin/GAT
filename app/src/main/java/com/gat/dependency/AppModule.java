package com.gat.dependency;

import android.app.Application;
import com.gat.app.screen.ScreenPresenterFactory;
import com.gat.data.book.BookDataSourceImpl;
import com.gat.data.firebase.FirebaseService;
import com.gat.data.firebase.FirebaseServiceImpl;
import com.gat.data.firebase.SignInFirebase;
import com.gat.data.firebase.SignInFirebaseImpl;
import com.gat.data.message.LocalMessageDataSourceImpl;
import com.gat.data.message.NetworkMessageDataSourceImpl;
import com.gat.data.user.PaperUserDataSource;
import com.gat.data.user.UserDataSourceImpl;
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
import com.gat.repository.impl.MessageRepositoryImpl;
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
    private final String BASE_URL = "http://gat-test-ver1.azurewebsites.net/api/";
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
        return new BookDataSourceImpl(dataComponent);
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
        return new UserDataSourceImpl(dataComponent);
    }

    @Provides
    @Singleton
    @Named("local")
    UserDataSource provideLocalUserDataSource(){
        return new PaperUserDataSource();
    }

    @Provides
    @Singleton
    FirebaseService provideFirebaseService(@Named("local") Lazy<UserDataSource> userDataSourceLazy, SchedulerFactory schedulerFactory) {
        return new FirebaseServiceImpl(userDataSourceLazy, schedulerFactory);
    }

    @Provides
    @Singleton
    SignInFirebase provideSignInFirebase(@Named("local") Lazy<UserDataSource> localUserDataSource, @Named("network") Lazy<UserDataSource> networkUserDataSource, FirebaseService firebaseService, SchedulerFactory schedulerFactory) {
        return new SignInFirebaseImpl(localUserDataSource, networkUserDataSource, firebaseService, schedulerFactory);
    }
    @Provides
    @Singleton
    @Named("network")
    MessageDataSource provideNetworkMessageDataSource(FirebaseService firebaseService) {
        return new NetworkMessageDataSourceImpl(firebaseService);
    }

    @Provides
    @Singleton
    @Named("local")
    MessageDataSource provideLocalMessageDataSource() {
        return new LocalMessageDataSourceImpl();
    }

    @Provides
    @Singleton
    MessageRepository provideMessageRepository(@Named("network")Lazy<MessageDataSource> networkDataSourceLazy,
                                               @Named("local")Lazy<MessageDataSource> localDataSourceLazy,
                                               @Named("network") Lazy<UserDataSource> networkUserDataSourceLazy,
                                               @Named("local") Lazy<UserDataSource> localUserDataSourceLazy){
        return new MessageRepositoryImpl(networkDataSourceLazy, localDataSourceLazy, networkUserDataSourceLazy, localUserDataSourceLazy);
    }

    @Provides
    @Singleton
    UserRepository provideUserRepository(@Named("network")Lazy<UserDataSource> networkDataSourceLazy,
                                         @Named("local")Lazy<UserDataSource> localDataSourceLazy,
                                         @Named("local") Lazy<MessageDataSource> localMessageDataSourceLazy,
                                         @Named("network") Lazy<MessageDataSource> networkMessageDataSourceLazy,
                                         SignInFirebase signInFirebase){
        return new UserRepositoryImpl(networkDataSourceLazy, localDataSourceLazy, localMessageDataSourceLazy,networkMessageDataSourceLazy, signInFirebase);
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
