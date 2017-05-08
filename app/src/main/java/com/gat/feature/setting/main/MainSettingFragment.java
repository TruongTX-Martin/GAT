package com.gat.feature.setting.main;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.gat.R;
import com.gat.app.fragment.ScreenFragment;
import com.gat.common.util.MZDebug;
import com.gat.feature.setting.ISettingDelegate;
import com.gat.repository.entity.User;
import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by mryit on 5/5/2017.
 */

@SuppressLint("ValidFragment")
public class MainSettingFragment extends ScreenFragment<MainSettingScreen, MainSettingPresenter> {

    @BindView(R.id.button_add_email_password)
    RelativeLayout buttonAddEmailPassword;

    @BindView(R.id.button_change_password)
    RelativeLayout buttonChangePassword;

    @BindView(R.id.text_view_facebook_account)
    TextView textViewFacebook;

    @BindView(R.id.text_view_twitter_account)
    TextView textViewTwitter;

    @BindView(R.id.text_view_google_account)
    TextView textViewGoogle;

    private ISettingDelegate delegate;
    private CompositeDisposable disposable;

    public MainSettingFragment (ISettingDelegate delegate) {
        this.delegate = delegate;;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.frame_setting_main;
    }

    @Override
    protected Object getPresenterKey() {
        return MainSettingScreen.instance();
    }

    @Override
    protected Class<MainSettingPresenter> getPresenterClass() {
        return MainSettingPresenter.class;
    }

    @Override
    protected MainSettingScreen getDefaultScreen() {
        return MainSettingScreen.instance();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        disposable = new CompositeDisposable(
                getPresenter().onUserInfoSuccess().subscribe(this::onLoadUserSuccess)
        );

        getPresenter().loadUserInfo();
    }

    @Override
    public void onDestroy() {
        disposable.dispose();
        super.onDestroy();
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
        if (mUser == null) {
            MZDebug.w("_______________________________________ User info null");
            return;
        }
        delegate.goToEditProfile(mUser);
    }

    @OnClick(R.id.button_facebook)
    void onFacebookTap () {
        if (mUser == null) {
            return;
        }
        if ( ! TextUtils.isEmpty(mUser.faceBookId())) {
            delegate.goToFacebook(mUser.faceBookName());
            return;
        }

        // connect facebook account

    }

    @OnClick(R.id.button_twitter)
    void onTwitterTap () {
        if (mUser == null) {
            return;
        }
        if ( ! TextUtils.isEmpty(mUser.twitterId())) {
            delegate.goToTwitter(mUser.twitterName());
            return;
        }

        // connect twitter account
    }

    @OnClick(R.id.button_google)
    void onGoogleTap () {
        if (mUser == null) {
            return;
        }
        if ( ! TextUtils.isEmpty(mUser.googleId())) {
            delegate.goToGoogle(mUser.googleName());
            return;
        }

        // connect google account
    }

    @OnClick(R.id.button_logout)
    void onLogoutTap () {
        Toast.makeText(mContext, "dialog hỏi muốn thoát ko", Toast.LENGTH_SHORT).show();
    }

    private User mUser;
    private void onLoadUserSuccess (User user ) {
        mUser = user;
        if (null == user) {
            return;
        }

        // setup ui

        // show password button
        if (user.passwordFlag() == 1) {
            buttonAddEmailPassword.setVisibility(View.GONE);
            buttonChangePassword.setVisibility(View.VISIBLE);
        } else {
            buttonAddEmailPassword.setVisibility(View.VISIBLE);
            buttonChangePassword.setVisibility(View.GONE);
        }

        // check facebook connected
        if (TextUtils.isEmpty(user.faceBookId()) && TextUtils.isEmpty(user.faceBookName())) {
            textViewFacebook.setText(user.faceBookName());
        }

        // check twitter connected
        if (TextUtils.isEmpty(user.faceBookId()) && TextUtils.isEmpty(user.faceBookName())) {
            textViewTwitter.setText(user.twitterName());
        }

        // check google connected
        if (TextUtils.isEmpty(user.faceBookId()) && TextUtils.isEmpty(user.faceBookName())) {
            textViewGoogle.setText(user.googleName());
        }
    }

}
