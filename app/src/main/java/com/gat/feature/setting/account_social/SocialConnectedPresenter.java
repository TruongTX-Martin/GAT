package com.gat.feature.setting.account_social;

import com.rey.mvp2.Presenter;

import io.reactivex.Observable;

/**
 * Created by mozaa on 05/05/2017.
 */

public interface SocialConnectedPresenter extends Presenter {

    void unLinkAccount (int type);
    Observable<String> onUnLinkAccountSocialSuccess ();

}
