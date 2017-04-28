package com.gat.dependency;

import com.gat.feature.bookdetail.BookDetailRequestPresenter;
import com.gat.feature.editinfo.EditInfoPresenter;

import com.gat.feature.book_detail.BookDetailPresenter;
import com.gat.feature.book_detail.add_to_bookcase.AddToBookcasePresenter;
import com.gat.feature.book_detail.comment.CommentPresenter;
import com.gat.feature.book_detail.list_user_sharing_book.ListUserSharingBookPresenter;
import com.gat.feature.book_detail.self_update_reading.SelfUpdateReadingPresenter;

import com.gat.feature.login.LoginPresenter;
import com.gat.feature.main.MainPresenter;
import com.gat.feature.message.presenter.GroupMessagePresenter;
import com.gat.feature.notification.NotificationPresenter;
import com.gat.feature.personal.PersonalPresenter;
import com.gat.feature.message.presenter.MessagePresenter;
import com.gat.feature.personaluser.PersonalUserPresenter;
import com.gat.feature.register.RegisterPresenter;
import com.gat.feature.register.update.category.AddCategoryPresenter;
import com.gat.feature.register.update.location.AddLocationPresenter;
import com.gat.feature.scanbarcode.ScanPresenter;
import com.gat.feature.search.SearchPresenter;
import com.gat.feature.suggestion.SuggestionPresenter;
import com.gat.feature.suggestion.nearby_user.ShareNearByUserDistancePresenter;
import com.gat.feature.suggestion.search.SuggestSearchPresenter;

import dagger.Subcomponent;

/**
 * Created by Rey on 2/15/2017.
 */
@Subcomponent(
    modules = {
        PresenterModule.class
    }
)
public interface PresenterComponent {

    SearchPresenter getSearchPresenter();
    LoginPresenter getLoginPresenter();
    RegisterPresenter getRegisterPresenter();
    AddLocationPresenter getAddLocationPresenter();
    AddCategoryPresenter getAddCategoryPresenter();
    SuggestionPresenter getSuggestionPresenter();
    MessagePresenter getMessagePresenter();
    GroupMessagePresenter getGroupMessagePresenter();
    MainPresenter getMainPresenter();
    ShareNearByUserDistancePresenter getShareNearByUserDistancePresenter();
    SuggestSearchPresenter getSuggestSearchPresenter();
    PersonalPresenter getPersonalPresenter();

    EditInfoPresenter getEditInfoPresenter();
    PersonalUserPresenter getPersonalUserPresenter();
    BookDetailRequestPresenter getBookDetailRequestPresenter();

    BookDetailPresenter getBookDetailPresenter();
    SelfUpdateReadingPresenter getSelfUpdateReadingPresenter();
    ListUserSharingBookPresenter getListUserSharingBookPresenter();
    AddToBookcasePresenter getAddToBookcasePresenter();
    CommentPresenter getCommentPresenter();

    ScanPresenter getScanPresenter();
    NotificationPresenter getNotificationPresenter();


}
