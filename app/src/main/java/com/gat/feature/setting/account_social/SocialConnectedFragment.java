package com.gat.feature.setting.account_social;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.TextView;
import android.widget.Toast;

import com.gat.R;
import com.gat.app.fragment.ScreenFragment;
import com.gat.common.util.ClientUtils;
import com.gat.feature.setting.ISettingDelegate;
import com.gat.feature.setting.KeyBackToMain;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by mozaa on 05/05/2017.
 */

@SuppressLint("ValidFragment")
public class SocialConnectedFragment extends ScreenFragment<SocialConnectedScreen, SocialConnectedPresenter> {

    @BindView(R.id.text_view_header)
    TextView textViewHeader;

    @BindView(R.id.text_view_social)
    TextView textViewSocial;

    private CompositeDisposable disposable;
    private ISettingDelegate delegate;
    private int mTypeSocial = 1;
    private String mSocialUsername = "";
    private android.support.v7.app.AlertDialog progressDialog;

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

        disposable = new CompositeDisposable(
                getPresenter().onUnLinkAccountSocialSuccess().subscribe(this::onDisconnectSuccess),
                getPresenter().onError().subscribe(this::onError)
        );

        switch (mTypeSocial) {
            case TypeSocial.FACEBOOK:
                textViewHeader.setText(R.string.facebook);
                textViewSocial.setText(getString(R.string.in_facebook_connection, mSocialUsername));
                break;

            case TypeSocial.TWITTER:
                textViewHeader.setText(R.string.twitter);
                textViewSocial.setText(getString(R.string.in_twitter_connection, mSocialUsername));
                break;

            case TypeSocial.GOOGLE:
                textViewHeader.setText(R.string.google);
                textViewSocial.setText(getString(R.string.in_google_connection, mSocialUsername));
                break;
        }
        progressDialog = ClientUtils.createLoadingDialog(getActivity());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        disposable.dispose();
        hideProgress();
        progressDialog = null;
    }

    @OnClick(R.id.image_view_back)
    void onBackPress() {
        delegate.goToMainSetting(KeyBackToMain.BACK_BUTTON);
    }


    @OnClick(R.id.button_disconnect_social)
    void onButtonDisconnectTap () {
        progressDialog.show();
        getPresenter().unLinkAccount(mTypeSocial);
    }


    private void onDisconnectSuccess (String message) {
        hideProgress();
        switch (mTypeSocial) {
            case TypeSocial.FACEBOOK:
                delegate.goToMainSetting(KeyBackToMain.DISCONNECT_FACEBOOK);
                break;

            case TypeSocial.TWITTER:
                delegate.goToMainSetting(KeyBackToMain.DISCONNECT_TWITTER);
                break;

            case TypeSocial.GOOGLE:
                delegate.goToMainSetting(KeyBackToMain.DISCONNECT_GOOGLE);
                break;
        }
    }

    private void onError (String message) {
        hideProgress();
        ClientUtils.showDialogError(getActivity(), getString(R.string.err), message);
    }

    private void hideProgress () {
        if (progressDialog.isShowing()) {
            progressDialog.hide();
        }
    }


}
