package com.gat.feature.setting.main;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.gat.R;
import com.gat.app.fragment.ScreenFragment;
import com.gat.common.util.MZDebug;
import com.gat.feature.setting.ISettingDelegate;
import com.gat.feature.setting.KeyBackToMain;
import com.gat.feature.setting.SocialType;
import com.gat.repository.entity.User;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by mryit on 5/5/2017.
 */

@SuppressLint("ValidFragment")
public class MainSettingFragment extends ScreenFragment<MainSettingScreen, MainSettingPresenter> {

    public static final String KEY_BACK_TO_MAIN = "key_back_to_main";
    private static final int RC_SIGN_IN = 9999;

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
    private GoogleApiClient mGoogleApiClient;
    private CallbackManager callbackManager;

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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        callbackManager = CallbackManager.Factory.create();
        createGoogleApiClient();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        disposable = new CompositeDisposable(
                getPresenter().onUserInfoSuccess().subscribe(this::onLoadUserSuccess),
                getPresenter().onConnectFacebookSuccess().subscribe(this::onConnectFacebookSuccess),
                getPresenter().onConnectGoogleSuccess().subscribe(this::onConnectGoogleSuccess),
                getPresenter().onConnectTwitterSuccess().subscribe(this::onConnectTwitterSuccess)
        );


        getPresenter().loadUserInfo();

        // update ui after back from another fragment
        int key = getArguments().getInt(KEY_BACK_TO_MAIN);
//        switch (key) {
//            case KeyBackToMain.FRIST_CREATE:
//                break;
//
//            case KeyBackToMain.BACK_BUTTON:
//                break;
//
//            case KeyBackToMain.DISCONNECT_FACEBOOK:
//                textViewFacebook.setText("");
//                break;
//
//            case KeyBackToMain.DISCONNECT_TWITTER:
//                textViewTwitter.setText("");
//                break;
//
//            case KeyBackToMain.DISCONNECT_GOOGLE:
//                textViewGoogle.setText("");
//                break;
//
//            case KeyBackToMain.ADD_EMAIL_PASSWORD:
//                break;
//
//        }

    }

    @Override
    public void onDestroy() {
        disposable.dispose();
        super.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (FacebookSdk.isFacebookRequestCode(requestCode)) {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        } else if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult googleSignInResult =
                    Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(googleSignInResult);
        }

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
        LoginManager.getInstance().logInWithReadPermissions(getActivity(), Arrays.asList("public_profile", "email"));
        LoginManager.getInstance().registerCallback(callbackManager, facebookCallback);
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
        Intent i = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(i, RC_SIGN_IN);
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
        if ( ! TextUtils.isEmpty(user.faceBookId()) && ! TextUtils.isEmpty(user.faceBookName())) {
            MZDebug.w("facebook connected: " + user.faceBookName());
            textViewFacebook.setText(user.faceBookName());
        }

        // check twitter connected
        if (! TextUtils.isEmpty(user.twitterId()) && ! TextUtils.isEmpty(user.twitterName())) {
            MZDebug.w("twitter connected: " + user.twitterName());
            textViewTwitter.setText(user.twitterName());
        }

        // check google connected
        if (! TextUtils.isEmpty(user.googleId()) && ! TextUtils.isEmpty(user.googleName())) {
            MZDebug.w("google connected: " + user.googleName());
            textViewGoogle.setText(user.googleName());
        }
    }


    private void createGoogleApiClient() {
        MZDebug.w("createGoogleApiClient");
        /*You can here also request also scopes*/
        GoogleSignInOptions gso = new GoogleSignInOptions.
                Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).
                requestScopes(new Scope(Scopes.EMAIL), new Scope(Scopes.PROFILE)).
                requestEmail().requestProfile().
                build();

        /*enableAutoManage : Enables automatic lifecycle management in a support library FragmentActivity that
        connects the client in onStart() and disconnects it in onStop().
        It handles user recoverable errors appropriately and calls if the ConnectionResult has no
        resolution.This eliminates most of the boiler plate associated with using GoogleApiClient.*/
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity()).
                addApi(Auth.GOOGLE_SIGN_IN_API, gso).
                build();
    }

    private void handleSignInResult(GoogleSignInResult googleSignInResult) {
        MZDebug.w(" __________________________________ handleSignInResult ______________________");

        if ( ! googleSignInResult.isSuccess()) {
            MZDebug.w("google sign in failed");
            return;
        }

        GoogleSignInAccount account = googleSignInResult.getSignInAccount();
        if (account == null) {
            MZDebug.w("google sign in Account = NULL");
            return;
        }

        MZDebug.w("account email:" + account.getEmail() + ", display name: " + account.getDisplayName() );
        getPresenter().requestConnectSocial(account.getEmail() , account.getDisplayName(), SocialType.GOOGLE);
    }


    FacebookCallback<LoginResult> facebookCallback = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {
            final AccessToken accessToken = loginResult.getAccessToken();
            GraphRequest request = GraphRequest.newMeRequest(accessToken,
                    (user, graphResponse) -> {
                        String fb_id = "";
                        String fb_name = "";

                        try {
                            fb_id = user.getString("id");
                            fb_name = user.getString("name");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        MZDebug.w("Facebook id: " + fb_id + ", name: " + fb_name);
                        getPresenter().requestConnectSocial(fb_id, fb_name, SocialType.FACEBOOK);
                    });
            Bundle parameters = new Bundle();
            parameters.putString("fields", "id, name");
            request.setParameters(parameters);
            request.executeAsync();
        }

        @Override
        public void onCancel() {
            MZDebug.i("facebook cancel");
        }

        @Override
        public void onError(FacebookException exception) {
            MZDebug.e(exception.toString());
        }
    };


    String userNameFacebook = "";
    private void onConnectFacebookSuccess (String username) {
        userNameFacebook= username;
        textViewFacebook.setText(username);
    }

    String userNameGoogle = "";
    private void onConnectGoogleSuccess (String username) {
        userNameGoogle = username;
        textViewGoogle.setText(username);
    }

    String userNameTwitter = "";
    private void onConnectTwitterSuccess (String username) {
        userNameTwitter = username;
        textViewTwitter.setText(username);
    }


}
