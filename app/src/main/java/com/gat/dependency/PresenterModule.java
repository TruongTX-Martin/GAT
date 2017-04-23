package com.gat.dependency;

import com.gat.domain.SchedulerFactory;
import com.gat.domain.UseCaseFactory;
import com.gat.feature.book_detail.BookDetailPresenter;
import com.gat.feature.book_detail.BookDetailPresenterImpl;
import com.gat.feature.book_detail.add_to_bookcase.AddToBookcasePresenter;
import com.gat.feature.book_detail.add_to_bookcase.AddToBookcasePresenterImpl;
import com.gat.feature.book_detail.comment.CommentPresenter;
import com.gat.feature.book_detail.comment.CommentPresenterImpl;
import com.gat.feature.book_detail.list_user_sharing_book.ListUserSharingBookPresenter;
import com.gat.feature.book_detail.list_user_sharing_book.ListUserSharingBookPresenterImpl;
import com.gat.feature.book_detail.self_update_reading.SelfUpdateReadingPresenter;
import com.gat.feature.book_detail.self_update_reading.SelfUpdateReadingPresenterImpl;
import com.gat.feature.login.LoginPresenter;
import com.gat.feature.login.LoginPresenterImpl;
import com.gat.feature.main.MainPresenter;
import com.gat.feature.main.MainPresenterImpl;
import com.gat.feature.personal.PersonalPresenter;
import com.gat.feature.personal.PersonalPresenterImpl;
import com.gat.feature.message.MessagePresenter;
import com.gat.feature.message.MessagePresenterImpl;
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
    SuggestionPresenter provideSuggestionPresenter(UseCaseFactory useCaseFactory,
                                                    SchedulerFactory schedulerFactory){
        return new SuggestionPresenterImpl(useCaseFactory, schedulerFactory) {
        };
    }

    @Provides
    MessagePresenter provideMessagePresenter(UseCaseFactory useCaseFactory,
                                             SchedulerFactory schedulerFactory){
        return new MessagePresenterImpl(useCaseFactory, schedulerFactory) {
        };
    }

    @Provides
    MainPresenter provideMainPresenter(UseCaseFactory useCaseFactory,
                                       SchedulerFactory schedulerFactory){
        return new MainPresenterImpl(useCaseFactory, schedulerFactory) {
        };
    }

    @Provides
    ShareNearByUserDistancePresenter provideShareNearByUserDistance(UseCaseFactory useCaseFactory,
                                                                    SchedulerFactory schedulerFactory){
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
                                               SchedulerFactory schedulerFactory){
        return  new PersonalPresenterImpl(useCaseFactory,schedulerFactory);
    }

    @Provides
    BookDetailPresenter provideBookDetailPresenter(UseCaseFactory useCaseFactory,
                                                   SchedulerFactory schedulerFactory){
        return  new BookDetailPresenterImpl(useCaseFactory,schedulerFactory);
    }

    @Provides
    SelfUpdateReadingPresenter provideSelfUpdateReadingPresenter(UseCaseFactory useCaseFactory,
                                                                 SchedulerFactory schedulerFactory) {
        return new SelfUpdateReadingPresenterImpl(useCaseFactory, schedulerFactory);
    }

    @Provides
    ListUserSharingBookPresenter provideListUserSharingBookPresenter(UseCaseFactory useCaseFactory,
                                                                     SchedulerFactory schedulerFactory) {
        return new ListUserSharingBookPresenterImpl(useCaseFactory, schedulerFactory);
    }

    @Provides
    AddToBookcasePresenter provideAddToBookcasePresenter (UseCaseFactory useCaseFactory,
                                                          SchedulerFactory schedulerFactory) {
        return new AddToBookcasePresenterImpl(useCaseFactory, schedulerFactory);
    }

    @Provides
    CommentPresenter provideCommentPresenter (UseCaseFactory useCaseFactory,
                                              SchedulerFactory schedulerFactory) {
        return new CommentPresenterImpl(useCaseFactory, schedulerFactory);
    }

}
