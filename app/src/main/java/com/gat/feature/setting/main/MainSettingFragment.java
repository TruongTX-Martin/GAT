package com.gat.feature.setting.main;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Toast;
import com.gat.R;
import com.gat.app.fragment.ScreenFragment;
import com.gat.feature.setting.ISettingDelegate;
import butterknife.OnClick;

/**
 * Created by mryit on 5/5/2017.
 */

@SuppressLint("ValidFragment")
public class MainSettingFragment extends ScreenFragment<MainSettingScreen, MainSettingPresenter> {

    private ISettingDelegate delegate;

    public MainSettingFragment (ISettingDelegate delegate) {
        this.delegate = delegate;;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.frame_setting_main;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @OnClick(R.id.button_add_email_password)
    void onAddEmailPasswordTap () {
        delegate.goToAddEmailPassword();
    }

    @OnClick(R.id.button_change_password)
    void onChangePasswordTap () {
        delegate.goToChangePassword();
    }

    @OnClick(R.id.button_edit_profile)
    void editProfile () {
        delegate.goToEditProfile();
    }

    @OnClick(R.id.button_facebook)
    void onFacebookTap () {
        delegate.goToFacebook();
    }

    @OnClick(R.id.button_twitter)
    void onTwitterTap () {
        delegate.goToTwitter();
    }

    @OnClick(R.id.button_google)
    void onGoogleTap () {
        delegate.goToGoogle();
    }

    @OnClick(R.id.button_logout)
    void onLogoutTap () {
        Toast.makeText(mContext, "dialog hỏi muốn thoát ko", Toast.LENGTH_SHORT).show();
    }

}
