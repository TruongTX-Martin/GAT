package com.gat.feature.setting.main;

import com.gat.repository.entity.User;
import com.rey.mvp2.Presenter;
import io.reactivex.Observable;

/**
 * Created by mryit on 5/5/2017.
 */

public interface MainSettingPresenter extends Presenter {

    void loadUserInfo ();
    Observable<User> onUserInfoSuccess ();




}
