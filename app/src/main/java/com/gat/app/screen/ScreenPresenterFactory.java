package com.gat.app.screen;

import com.gat.dependency.PresenterComponent;
import com.gat.feature.login.LoginScreen;
import com.gat.feature.personal.PersonalScreen;
import com.gat.feature.register.RegisterScreen;
import com.gat.feature.register.update.category.AddCategoryScreen;
import com.gat.feature.register.update.location.AddLocationScreen;
import com.gat.feature.search.SearchScreen;
import com.rey.mvp2.Presenter;
import com.rey.mvp2.PresenterFactory;

/**
 * Created by Rey on 2/14/2017.
 */

public class ScreenPresenterFactory implements PresenterFactory {

    private final PresenterComponent presenterComponent;

    public ScreenPresenterFactory(PresenterComponent presenterComponent){
        this.presenterComponent = presenterComponent;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <P extends Presenter> P getPresenter(Object key, Class<P> presenterClass) {
        if(key instanceof SearchScreen)
            return (P)presenterComponent.getSearchPresenter();
        else if (key instanceof LoginScreen)
            return (P)presenterComponent.getLoginPresenter();
        else if (key instanceof RegisterScreen)
            return (P)presenterComponent.getRegisterPresenter();
        else if (key instanceof AddLocationScreen)
            return (P)presenterComponent.getAddLocationPresenter();
        else if (key instanceof AddCategoryScreen)
            return (P) presenterComponent.getAddCategoryPresenter();
        else if (key instanceof PersonalScreen){
            return (P) presenterComponent.getPersonalPresenter();
        }
        throw new IllegalArgumentException("Not support key " + key);
    }
}
