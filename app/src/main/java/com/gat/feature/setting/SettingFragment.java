package com.gat.feature.setting;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.widget.FrameLayout;
import com.gat.R;
import com.gat.app.fragment.ScreenFragment;
import com.gat.common.util.Views;
import com.gat.feature.setting.add_email_password.AddEmailPasswordFragment;
import com.gat.feature.setting.main.MainSettingFragment;
import butterknife.BindView;

/**
 * Created by mryit on 4/25/2017.
 */

public class SettingFragment extends ScreenFragment<SettingScreen, SettingPresenter>
        implements ISettingDelegate {

    @BindView(R.id.fl_setting)
    FrameLayout frameLayout;


    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_setting;
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
                R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public void goToAddEmailPassword() {
        Views.navigationToView(getActivity(), new AddEmailPasswordFragment(this), R.id.fl_setting,
                R.anim.slide_in_right, R.anim.slide_out_left);
    }

    @Override
    public void goToEditProfile() {

    }

    @Override
    public void goToChangePassword() {

    }

    @Override
    public void goToFacebook() {

    }

    @Override
    public void goToTwitter() {

    }

    @Override
    public void goToGoogle() {

    }

    @Override
    public void logout() {

    }


}
