package com.gat.dependency;

import com.gat.domain.SchedulerFactory;
import com.gat.domain.UseCaseFactory;
import com.gat.feature.bookdetail.BookDetailRequestPresenter;
import com.gat.feature.bookdetail.BookDetailRequestPresenterImpl;
import com.gat.feature.editinfo.EditInfoPresenter;
import com.gat.feature.editinfo.EditInfoPresenterImpl;
import com.gat.feature.login.LoginPresenter;
import com.gat.feature.login.LoginPresenterImpl;
import com.gat.feature.main.MainPresenter;
import com.gat.feature.main.MainPresenterImpl;
import com.gat.feature.personal.PersonalPresenter;
import com.gat.feature.personal.PersonalPresenterImpl;
import com.gat.feature.personaluser.PersonalUserPresenter;
import com.gat.feature.personaluser.PersonalUserPresenterImpl;
import com.gat.feature.register.RegisterPresenter;
import com.gat.feature.register.RegisterPresenterImpl;
import com.gat.feature.register.update.category.AddCategoryPresenter;
import com.gat.feature.register.update.category.AddCategoryPresenterImpl;
import com.gat.feature.register.update.location.AddLocationPresenter;
import com.gat.feature.register.update.location.AddLocationPresenterImpl;
import com.gat.feature.search.SearchItemBuilder;
import com.gat.feature.search.SearchPresenter;
import com.gat.feature.search.SearchPresenterImpl;
import com.gat.feature.suggestion.SuggestionPresenter;
import com.gat.feature.suggestion.SuggestionPresenterImpl;
import com.gat.feature.suggestion.nearby_user.ShareNearByUserDistancePresenter;
import com.gat.feature.suggestion.nearby_user.ShareNearByUserDistancePresenterImpl;
import com.gat.feature.suggestion.search.SuggestSearchPresenter;
import com.gat.feature.suggestion.search.SuggestSearchPresenterImpl;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Rey on 2/15/2017.
 */
@Module
public class PresenterModule {

    @Provides
    SearchItemBuilder provideSearchItemBuilder() {
        return new SearchItemBuilder();
    }

    @Provides
    SearchPresenter provideSearchPresenter(UseCaseFactory useCaseFactory,
                                           SchedulerFactory schedulerFactory,
                                           SearchItemBuilder itemBuilder) {
        return new SearchPresenterImpl(useCaseFactory, schedulerFactory, itemBuilder);
    }

    @Provides
    LoginPresenter provideLoginPresenter(UseCaseFactory useCaseFactory,
                                         SchedulerFactory schedulerFactory) {
        return new LoginPresenterImpl(useCaseFactory, schedulerFactory);
    }

    @Provides
    RegisterPresenter provideRegisterPresenter(UseCaseFactory useCaseFactory,
                                               SchedulerFactory schedulerFactory) {
        return new RegisterPresenterImpl(useCaseFactory, schedulerFactory);
    }

    @Provides
    AddLocationPresenter provideAddLocationPresenter(UseCaseFactory useCaseFactory,
                                                     SchedulerFactory schedulerFactory) {
        return new AddLocationPresenterImpl(useCaseFactory, schedulerFactory);
    }

    @Provides
    AddCategoryPresenter provideAddCategoryPresenter(UseCaseFactory useCaseFactory,
                                                     SchedulerFactory schedulerFactory) {
        return new AddCategoryPresenterImpl(useCaseFactory, schedulerFactory) {
        };
    }

    @Provides
    SuggestionPresenter provideSuggestionPresenter(UseCaseFactory useCaseFactory,
                                                   SchedulerFactory schedulerFactory) {
        return new SuggestionPresenterImpl(useCaseFactory, schedulerFactory) {
        };
    }

    @Provides
    MainPresenter provideMainPresenter(UseCaseFactory useCaseFactory,
                                       SchedulerFactory schedulerFactory) {
        return new MainPresenterImpl(useCaseFactory, schedulerFactory) {
        };
    }

    @Provides
    ShareNearByUserDistancePresenter provideShareNearByUserDistance(UseCaseFactory useCaseFactory,
                                                                    SchedulerFactory schedulerFactory) {
        return new ShareNearByUserDistancePresenterImpl(useCaseFactory, schedulerFactory) {
        };
    }

    @Provides
    SuggestSearchPresenter provideSuggestSearchPresenter(UseCaseFactory useCaseFactory,
                                                         SchedulerFactory schedulerFactory) {
        return new SuggestSearchPresenterImpl(useCaseFactory, schedulerFactory);
    }

    @Provides
    PersonalPresenter providePersonalPresenter(UseCaseFactory useCaseFactory,
                                               SchedulerFactory schedulerFactory) {
        return new PersonalPresenterImpl(useCaseFactory, schedulerFactory);
    }

    @Provides
    EditInfoPresenter provideEditInfoPresenter(UseCaseFactory useCaseFactory,
                                               SchedulerFactory schedulerFactory) {
        return new EditInfoPresenterImpl(useCaseFactory, schedulerFactory);
    }

    @Provides
    PersonalUserPresenter providePersonalUserPresenter(UseCaseFactory useCaseFactory,
                                                       SchedulerFactory schedulerFactory) {
        return new PersonalUserPresenterImpl(useCaseFactory, schedulerFactory);
    }

    @Provides
    BookDetailRequestPresenter provideBookDetailPresenter(UseCaseFactory useCaseFactory,
                                                          SchedulerFactory schedulerFactory) {
        return new BookDetailRequestPresenterImpl(useCaseFactory, schedulerFactory);
    }

}
