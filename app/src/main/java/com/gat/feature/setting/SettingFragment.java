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
        transaction.replace(R.id.fl_setting, new MainSettingFragment(this));
        transaction.commit();
    }


    @Override
    public void goToMainSetting() {
        Views.navigationToView(getActivity(), new MainSettingFragment(this), R.id.fl_setting,
                R.anim.slide_in_right, R.anim.slide_out_left);
    }

    @Override
    public void goToAddEmailPassword() {
        Views.navigationToView(getActivity(), new AddEmailPasswordFragment(this), R.id.fl_setting,
                R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public void goToEditProfile() {

    }

    @Override
    public void goToChangePassword() {
        Views.navigationToView(getActivity(), new ChangePasswordFragment(this), R.id.fl_setting,
                R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public void goToFacebook() {
        Views.navigationToView(getActivity(), new SocialConnectedFragment(this, TypeSocial.FACEBOOK), R.id.fl_setting,
                R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public void goToTwitter() {
        Views.navigationToView(getActivity(), new SocialConnectedFragment(this, TypeSocial.TWITTER), R.id.fl_setting,
                R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public void goToGoogle() {
        Views.navigationToView(getActivity(), new SocialConnectedFragment(this, TypeSocial.GOOGLE), R.id.fl_setting,
                R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public void logout() {

    }


}
