package com.gat.dependency;

import android.app.Application;

import com.gat.domain.SchedulerFactory;
import com.gat.domain.UseCaseFactory;
import com.rey.mvp2.PresenterManager;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by Rey on 12/7/2015.
 */
@Singleton
@Component(
    modules = {
        AppModule.class
    }
)
public interface AppComponent {

    PresenterComponent plus(PresenterModule module);

    Application getApplication();

    SchedulerFactory getSchedulerFactory();

    UseCaseFactory getUseCaseFactory();

    PresenterManager getPresenterManager();
}
