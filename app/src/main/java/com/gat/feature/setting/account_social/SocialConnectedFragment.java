package com.gat.feature.setting.account_social;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import com.gat.R;
import com.gat.app.fragment.ScreenFragment;
import com.gat.feature.setting.ISettingDelegate;
import com.gat.feature.setting.KeyBackToMain;

import butterknife.OnClick;

/**
 * Created by mozaa on 05/05/2017.
 */

@SuppressLint("ValidFragment")
public class SocialConnectedFragment extends ScreenFragment<SocialConnectedScreen, SocialConnectedPresenter> {

    private ISettingDelegate delegate;
    private int mTypeSocial = 1;
    private String mSocialUsername = "";

    public SocialConnectedFragment (ISettingDelegate delegate, int type_social, String username) {
        this.delegate = delegate;
        this.mTypeSocial = type_social;
        this.mSocialUsername = username;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.frame_setting_social_connected;
    }

    @Override
    protected Object getPresenterKey() {
        return SocialConnectedScreen.instance();
    }

    @Override
    protected Class<SocialConnectedPresenter> getPresenterClass() {
        return SocialConnectedPresenter.class;
    }

    @Override
    protected SocialConnectedScreen getDefaultScreen() {
        return SocialConnectedScreen.instance();
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        switch (mTypeSocial) {
            case TypeSocial.FACEBOOK:
                break;

            case TypeSocial.TWITTER:
                break;

            case TypeSocial.GOOGLE:
                break;
        }

    }


    @OnClick(R.id.image_view_back)
    void onBackPress() {
        delegate.goToMainSetting(KeyBackToMain.BACK_BUTTON);
    }



}
