package com.gat.feature.setting;

import com.gat.repository.entity.User;

/**
 * Created by mryit on 5/5/2017.
 */

public interface ISettingDelegate {

    void goToMainSetting (int keyBackToMain);

    void goToAddEmailPassword ();

    void goToEditProfile (User user);

    void goToChangePassword ();

    void goToFacebook (String username);

    void goToTwitter (String username);

    void goToGoogle (String username);

    void logout ();

}
