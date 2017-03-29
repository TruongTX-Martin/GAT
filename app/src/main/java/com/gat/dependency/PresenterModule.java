package com.gat.dependency;

import com.gat.domain.SchedulerFactory;
import com.gat.domain.UseCaseFactory;
import com.gat.feature.login.LoginPresenter;
import com.gat.feature.login.LoginPresenterImpl;
import com.gat.feature.personal.PersonalPresenter;
import com.gat.feature.personal.PersonalPresenterImpl;
import com.gat.feature.register.RegisterPresenter;
import com.gat.feature.register.RegisterPresenterImpl;
import com.gat.feature.register.update.category.AddCategoryPresenter;
import com.gat.feature.register.update.category.AddCategoryPresenterImpl;
import com.gat.feature.register.update.location.AddLocationPresenter;
import com.gat.feature.register.update.location.AddLocationPresenterImpl;
import com.gat.feature.search.SearchItemBuilder;
import com.gat.feature.search.SearchPresenter;
import com.gat.feature.search.SearchPresenterImpl;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Rey on 2/15/2017.
 */
@Module
public class PresenterModule {

    @Provides
    SearchItemBuilder provideSearchItemBuilder(){
        return new SearchItemBuilder();
    }

    @Provides
    SearchPresenter provideSearchPresenter(UseCaseFactory useCaseFactory,
                                           SchedulerFactory schedulerFactory,
                                           SearchItemBuilder itemBuilder){
        return new SearchPresenterImpl(useCaseFactory, schedulerFactory, itemBuilder);
    }

    @Provides
    LoginPresenter provideLoginPresenter(UseCaseFactory useCaseFactory,
                                         SchedulerFactory schedulerFactory){
        return new LoginPresenterImpl(useCaseFactory, schedulerFactory);
    }

    @Provides
    RegisterPresenter provideRegisterPresenter(UseCaseFactory useCaseFactory,
                                               SchedulerFactory schedulerFactory){
        return new RegisterPresenterImpl(useCaseFactory, schedulerFactory);
    }

    @Provides
    AddLocationPresenter provideAddLocationPresenter(UseCaseFactory useCaseFactory,
                                                     SchedulerFactory schedulerFactory){
        return new AddLocationPresenterImpl(useCaseFactory, schedulerFactory);
    }

    @Provides
    AddCategoryPresenter provideAddCategoryPresenter(UseCaseFactory useCaseFactory,
                                                     SchedulerFactory schedulerFactory){
        return new AddCategoryPresenterImpl(useCaseFactory, schedulerFactory) {
        };
    }

    @Provides
    PersonalPresenter providePersonalPresenter(UseCaseFactory useCaseFactory,
                                               SchedulerFactory schedulerFactory){
        return  new PersonalPresenterImpl(useCaseFactory,schedulerFactory);
    }
}
