package com.gat.feature.setting;

import android.content.Intent;
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
import com.gat.common.util.Constance;
import com.gat.common.util.Views;
import com.gat.feature.editinfo.EditInfoActivity;
import com.gat.feature.main.MainActivity;
import com.gat.feature.setting.account_social.SocialConnectedFragment;
import com.gat.feature.setting.account_social.TypeSocial;
import com.gat.feature.setting.add_email_password.AddEmailPasswordFragment;
import com.gat.feature.setting.change_password.ChangePasswordFragment;
import com.gat.feature.setting.main.MainSettingFragment;
import com.gat.repository.entity.User;

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
        Bundle args = new Bundle();
        args.putInt(MainSettingFragment.KEY_BACK_TO_MAIN, KeyBackToMain.FRIST_CREATE);
        getMainSettingFragment().setArguments(args);
        transaction.replace(R.id.fl_setting, getMainSettingFragment());
        transaction.commit();
    }

    @Override
    public void goToMainSetting(int key) {

        //mainSettingFragment = new MainSettingFragment(this);
        FragmentManager manager = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
        Bundle args = new Bundle();
        args.putInt(MainSettingFragment.KEY_BACK_TO_MAIN, key);
        getMainSettingFragment().setArguments(args);
        transaction.replace(R.id.fl_setting, getMainSettingFragment());
        transaction.commit();
    }

    @Override
    public void goToAddEmailPassword() {
        Views.navigationToView(getActivity(), getAddEmailPasswordFragment(),
                R.id.fl_setting, R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public void goToEditProfile(User user) {
        Intent intent = new Intent(getActivity().getApplicationContext(), EditInfoActivity.class);
        intent.putExtra("UserInfo",  user);
        getActivity().startActivity(intent);
    }

    @Override
    public void goToChangePassword() {
        Views.navigationToView(getActivity(), getChangePasswordFragment(),
                R.id.fl_setting, R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public void goToFacebook(String username) {
        Views.navigationToView(getActivity(), getSocialFacebook(username),
                R.id.fl_setting, R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public void goToTwitter(String username) {
        Views.navigationToView(getActivity(), getSocialTwitter(username),
                R.id.fl_setting, R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public void goToGoogle(String username) {
        Views.navigationToView(getActivity(), getSocialGoogle(username),
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

    private SocialConnectedFragment getSocialFacebook(String username) {
        if (null == socialFacebook) {
            socialFacebook = new SocialConnectedFragment(this, TypeSocial.FACEBOOK, username);
        }
        return socialFacebook;
    }

    private SocialConnectedFragment getSocialTwitter(String username) {
        if (null == socialTwitter) {
            socialTwitter = new SocialConnectedFragment(this, TypeSocial.TWITTER, username);
        }
        return socialTwitter;
    }

    private SocialConnectedFragment getSocialGoogle(String username) {
        if (null == socialGoogle) {
            socialGoogle = new SocialConnectedFragment(this, TypeSocial.GOOGLE, username);
        }
        return socialGoogle;
    }


}
