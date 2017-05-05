package com.gat.feature.setting.add_email_password;

import android.annotation.SuppressLint;

import com.gat.R;
import com.gat.app.fragment.ScreenFragment;
import com.gat.feature.setting.ISettingDelegate;

import butterknife.OnClick;

/**
 * Created by mryit on 5/5/2017.
 */

@SuppressLint("ValidFragment")
public class AddEmailPasswordFragment extends ScreenFragment<AddEmailPasswordScreen, AddEmailPasswordPresenter> {

    private ISettingDelegate delegate;

    public AddEmailPasswordFragment(ISettingDelegate delegate) {
        this.delegate = delegate;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.frame_add_email_password;
    }

    @Override
    protected AddEmailPasswordScreen getDefaultScreen() {
        return AddEmailPasswordScreen.instance();
    }

    @Override
    protected Class<AddEmailPasswordPresenter> getPresenterClass() {
        return AddEmailPasswordPresenter.class;
    }

    @Override
    protected Object getPresenterKey() {
        return AddEmailPasswordScreen.instance();
    }

    @OnClick(R.id.image_view_back)
    void onBackPress() {
        delegate.goToMainSetting();
    }


}
