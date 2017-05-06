package com.gat.feature.setting;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import com.gat.R;
import com.gat.app.fragment.ScreenFragment;
import com.gat.common.util.Views;
import com.gat.feature.setting.account_social.SocialConnectedFragment;
import com.gat.feature.setting.account_social.TypeSocial;
import com.gat.feature.setting.add_email_password.AddEmailPasswordFragment;
import com.gat.feature.setting.change_password.ChangePasswordFragment;
import com.gat.feature.setting.main.MainSettingFragment;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mryit on 4/25/2017.
 */

public class SettingFragment extends Fragment implements ISettingDelegate {

    @BindView(R.id.fl_setting)
    FrameLayout frameLayout;

    private MainSettingFragment mainSettingFragment;
    private AddEmailPasswordFragment addEmailPasswordFragment;
    private ChangePasswordFragment changePasswordFragment;
    private SocialConnectedFragment socialFacebook;
    private SocialConnectedFragment socialTwitter;
    private SocialConnectedFragment socialGoogle;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, container, false);

        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // first page is main fragment page
        FragmentManager manager = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.fl_setting, getMainSettingFragment());
        transaction.commit();
    }

    @Override
    public void goToMainSetting() {
        Views.navigationToView(getActivity(), getMainSettingFragment(),
                R.id.fl_setting, R.anim.slide_in_right, R.anim.slide_out_left);
    }

    @Override
    public void goToAddEmailPassword() {
        Views.navigationToView(getActivity(), getAddEmailPasswordFragment(),
                R.id.fl_setting, R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public void goToEditProfile() {

    }

    @Override
    public void goToChangePassword() {
        Views.navigationToView(getActivity(), getChangePasswordFragment(),
                R.id.fl_setting, R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public void goToFacebook() {
        Views.navigationToView(getActivity(), getSocialFacebook(),
                R.id.fl_setting, R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public void goToTwitter() {
        Views.navigationToView(getActivity(), getSocialTwitter(),
                R.id.fl_setting, R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public void goToGoogle() {
        Views.navigationToView(getActivity(), getSocialGoogle(),
                R.id.fl_setting, R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public void logout() {

    }


    private MainSettingFragment getMainSettingFragment() {
        if (null == mainSettingFragment) {
            mainSettingFragment = new  MainSettingFragment(this);
        }
        return mainSettingFragment;
    }

    private AddEmailPasswordFragment getAddEmailPasswordFragment() {
        if (null == addEmailPasswordFragment) {
            addEmailPasswordFragment = new AddEmailPasswordFragment(this);
        }
        return addEmailPasswordFragment;
    }

    private ChangePasswordFragment getChangePasswordFragment() {
        if (null == changePasswordFragment) {
            changePasswordFragment = new ChangePasswordFragment(this);
        }
        return changePasswordFragment;
    }

    private SocialConnectedFragment getSocialFacebook() {
        if (null == socialFacebook) {
            socialFacebook = new SocialConnectedFragment(this, TypeSocial.FACEBOOK);
        }
        return socialFacebook;
    }

    private SocialConnectedFragment getSocialTwitter() {
        if (null == socialTwitter) {
            socialTwitter = new SocialConnectedFragment(this, TypeSocial.TWITTER);
        }
        return socialTwitter;
    }

    private SocialConnectedFragment getSocialGoogle() {
        if (null == socialGoogle) {
            socialGoogle = new SocialConnectedFragment(this, TypeSocial.GOOGLE);
        }
        return socialGoogle;
    }


}
