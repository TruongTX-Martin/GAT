package com.gat.feature.setting.change_password;

import android.annotation.SuppressLint;
import com.gat.R;
import com.gat.app.fragment.ScreenFragment;
import com.gat.feature.setting.ISettingDelegate;

import butterknife.OnClick;

/**
 * Created by mozaa on 05/05/2017.
 */

@SuppressLint("ValidFragment")
public class ChangePasswordFragment extends ScreenFragment<ChangePasswordScreen, ChangePasswordPresenter> {

    private ISettingDelegate delegate;

    public ChangePasswordFragment (ISettingDelegate delegate) {
        this.delegate = delegate;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.frame_setting_change_password;
    }

    @Override
    protected Object getPresenterKey() {
        return ChangePasswordScreen.instance();
    }

    @Override
    protected Class<ChangePasswordPresenter> getPresenterClass() {
        return ChangePasswordPresenter.class;
    }

    @Override
    protected ChangePasswordScreen getDefaultScreen() {
        return ChangePasswordScreen.instance();
    }


    @OnClick(R.id.image_view_back)
    void onBackPress() {
        delegate.goToMainSetting();
    }



}
