package com.gat.dependency;

import com.gat.feature.editinfo.EditInfoPresenter;
import com.gat.feature.login.LoginPresenter;
import com.gat.feature.main.MainPresenter;
import com.gat.feature.personal.PersonalPresenter;
import com.gat.feature.register.RegisterPresenter;
import com.gat.feature.register.update.category.AddCategoryPresenter;
import com.gat.feature.register.update.location.AddLocationPresenter;
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
    MainPresenter getMainPresenter();
    ShareNearByUserDistancePresenter getShareNearByUserDistancePresenter();
    SuggestSearchPresenter getSuggestSearchPresenter();
    PersonalPresenter getPersonalPresenter();

}
