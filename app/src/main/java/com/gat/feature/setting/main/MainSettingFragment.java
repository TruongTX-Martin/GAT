package com.gat.feature.setting.main;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.gat.R;
import com.gat.app.activity.ScreenActivity;
import com.gat.app.fragment.ScreenFragment;
import com.gat.common.util.ClientUtils;
import com.gat.common.util.MZDebug;
import com.gat.common.util.Strings;
import com.gat.common.util.Views;
import com.gat.feature.book_detail.BookDetailActivity;
import com.gat.feature.book_detail.self_update_reading.ReadingState;
import com.gat.feature.login.LoginScreen;
import com.gat.feature.main.MainActivity;
import com.gat.feature.setting.ISettingDelegate;
import com.gat.feature.setting.SocialType;
import com.gat.feature.start.StartActivity;
import com.gat.repository.entity.User;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;
import com.twitter.sdk.android.core.services.AccountService;
import org.json.JSONException;
import java.util.Arrays;
import butterknife.BindView;
import butterknife.OnClick;
import io.fabric.sdk.android.Fabric;
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

    @BindView(R.id.icon_facebook)
    ImageView imageViewFacebook;

    @BindView(R.id.icon_google)
    ImageView imageViewGoogle;

    @BindView(R.id.icon_twitter)
    ImageView imageViewTwitter;

    private ISettingDelegate delegate;
    private CompositeDisposable disposable;
    private GoogleApiClient mGoogleApiClient;
    private CallbackManager callbackManager;
    private TwitterAuthClient twitterAuthClient;
    private AlertDialog progressDialog;
    private MainActivity mainActivity;

    public MainSettingFragment () {}

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
        mainActivity = (MainActivity) getActivity();
        callbackManager = CallbackManager.Factory.create();
        createGoogleApiClient();
        createTwitterClient();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Views.hideKeyboard(getActivity());
        progressDialog = ClientUtils.createLoadingDialog(getActivity());

        disposable = new CompositeDisposable(
                getPresenter().onUserInfoSuccess().subscribe(this::onLoadUserSuccess),
                getPresenter().onConnectFacebookSuccess().subscribe(this::onConnectFacebookSuccess),
                getPresenter().onConnectGoogleSuccess().subscribe(this::onConnectGoogleSuccess),
                getPresenter().onConnectTwitterSuccess().subscribe(this::onConnectTwitterSuccess),
                getPresenter().onSignOutSuccess().subscribe(this::onSignOutSuccess),
                getPresenter().onError().subscribe(this::onError)
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
        } else {
            twitterAuthClient.onActivityResult(requestCode, resultCode, data);
        }

    }

    @OnClick(R.id.button_add_email_password)
    void onAddEmailPasswordTap () {
        if (mUser == null) {
            ClientUtils.showRequiredLoginDialog(mainActivity,mainActivity);
            return;
        }
        delegate.goToAddEmailPassword();
    }

    @OnClick(R.id.button_change_password)
    void onChangePasswordTap () {
        if (mUser == null) {
            ClientUtils.showRequiredLoginDialog(mainActivity,mainActivity);
            return;
        }
        delegate.goToChangePassword();
    }

    @OnClick(R.id.button_edit_profile)
    void editProfile () {
        if (mUser == null) {
            ClientUtils.showRequiredLoginDialog(mainActivity,mainActivity);
            return;
        }
        delegate.goToEditProfile(mUser);
    }

    @OnClick(R.id.button_facebook)
    void onFacebookTap () {
        if (mUser == null) {
            ClientUtils.showRequiredLoginDialog(mainActivity,mainActivity);
            return;
        }
        if ( ! TextUtils.isEmpty(mFacebookUserName)) {
            delegate.goToFacebook(mFacebookUserName);
            return;
        }

        // connect facebook account
        LoginManager.getInstance().logInWithReadPermissions(getActivity(), Arrays.asList("public_profile", "email"));
        LoginManager.getInstance().registerCallback(callbackManager, facebookCallback);
    }

    @OnClick(R.id.button_twitter)
    void onTwitterTap () {
        if (mUser == null) {
            ClientUtils.showRequiredLoginDialog(mainActivity,mainActivity);
            return;
        }
        if ( ! TextUtils.isEmpty(mTwitterUserName)) {
            delegate.goToTwitter(mTwitterUserName);
            return;
        }

        // connect twitter account
        openTwitterSignIn();
    }

    @OnClick(R.id.button_google)
    void onGoogleTap () {
        if (mUser == null) {
            ClientUtils.showRequiredLoginDialog(mainActivity, (ScreenActivity) mainActivity);
            return;
        }
        if ( ! TextUtils.isEmpty(mGoogleUserName)) {
            delegate.goToGoogle(mGoogleUserName);
            return;
        }

        // connect google account
        Intent i = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(i, RC_SIGN_IN);
    }

    @OnClick(R.id.button_logout)
    void onLogoutTap () {

        if (mUser == null) {
            MainActivity.startAndClear(getActivity(), StartActivity.class, LoginScreen.instance(Strings.EMPTY, true));
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getResources().getString(R.string.ask_for_logout));
        builder.setPositiveButton(android.R.string.yes, (dialog, id) -> {
            dialog.dismiss();
            progressDialog.show();
            getPresenter().requestSignOut();
        });
        builder.setNegativeButton(android.R.string.cancel, (dialog, id) -> dialog.dismiss());
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private User mUser;
    private void onLoadUserSuccess (User user ) {
        if (null == user || !user.isValid()) {
            return;
        }

        mUser = user;
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
        mFacebookUserName = user.faceBookName();
        if ( ! TextUtils.isEmpty(user.faceBookId()) && ! TextUtils.isEmpty(user.faceBookName())) {
            MZDebug.w("facebook connected: " + user.faceBookName());
            textViewFacebook.setText(user.faceBookName());
            imageViewFacebook.setImageResource(R.drawable.ic_facebook_active);
        } else {
            imageViewFacebook.setImageResource(R.drawable.ic_facebook);
        }

        // check twitter connected
        mTwitterUserName = user.twitterName();
        if (! TextUtils.isEmpty(user.twitterId()) && ! TextUtils.isEmpty(user.twitterName())) {
            MZDebug.w("twitter connected: " + user.twitterName());
            textViewTwitter.setText(user.twitterName());
            imageViewTwitter.setImageResource(R.drawable.ic_twitter_active);
        } else {
            imageViewTwitter.setImageResource(R.drawable.ic_twitter);
        }

        // check google connected
        mGoogleUserName = user.googleName();
        if (! TextUtils.isEmpty(user.googleId()) && ! TextUtils.isEmpty(user.googleName())) {
            MZDebug.w("google connected: " + user.googleName());
            textViewGoogle.setText(user.googleName());
            imageViewGoogle.setImageResource(R.drawable.ic_google_active);
        } else {
            imageViewGoogle.setImageResource(R.drawable.ic_google);
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

    private void createTwitterClient () {
        ApplicationInfo app = null;
        try {
            app = getActivity().getPackageManager().getApplicationInfo(getActivity().getPackageName(), PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (null == app) {
            return;
        }
        Bundle bundle = app.metaData;
        String apiKey = bundle.getString("io.fabric.ApiKey");
        String apiSecret = getString(R.string.twitter_api_secret);
        TwitterAuthConfig authConfig = new TwitterAuthConfig(
                apiKey,
                apiSecret);
        Fabric.with(getActivity(), new Twitter(authConfig));
        twitterAuthClient = new TwitterAuthClient();
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
        progressDialog.show();
        getPresenter().requestConnectSocial(account.getEmail() , account.getDisplayName(), SocialType.GOOGLE);
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                status -> {

                });
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
                        progressDialog.show();
                        getPresenter().requestConnectSocial(fb_id, fb_name, SocialType.FACEBOOK);
                        LoginManager.getInstance().logOut();
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

    private void openTwitterSignIn () {
        if (null == twitterAuthClient) {
            return;
        }
        twitterAuthClient.authorize(getActivity(), new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> resultSession) {
                TwitterSession session = resultSession.data;
                AccountService service = Twitter.getApiClient(session).getAccountService();
                service.verifyCredentials(true, true).enqueue(new Callback<com.twitter.sdk.android.core.models.User>() {
                    @Override
                    public void success(Result<com.twitter.sdk.android.core.models.User> resultUser) {
                        progressDialog.show();
                        getPresenter().requestConnectSocial(
                                String.valueOf(resultUser.data.getId()), resultUser.data.name, SocialType.TWITTER);
                    }

                    @Override
                    public void failure(TwitterException ex) {
                        MZDebug.w("Twitter failure: " + Log.getStackTraceString(ex));
                    }
                });
            }

            @Override
            public void failure(TwitterException ex) {
                MZDebug.w("Twitter failure: " + Log.getStackTraceString(ex));
            }
        });
    }

    String mFacebookUserName = "";
    private void onConnectFacebookSuccess (String username) {
        hideProgress();
        mFacebookUserName = username;
        textViewFacebook.setText(username);
        imageViewFacebook.setImageResource(R.drawable.ic_facebook_active);
    }

    String mGoogleUserName = "";
    private void onConnectGoogleSuccess (String username) {
        hideProgress();
        mGoogleUserName = username;
        textViewGoogle.setText(username);
        imageViewGoogle.setImageResource(R.drawable.ic_google_active);
    }

    String mTwitterUserName = "";
    private void onConnectTwitterSuccess (String username) {
        hideProgress();
        mTwitterUserName = username;
        textViewTwitter.setText(username);
        imageViewTwitter.setImageResource(R.drawable.ic_twitter_active);
    }

    private void onSignOutSuccess (Boolean result) {
        hideProgress();
        MainActivity.startAndClear(getActivity(), StartActivity.class, LoginScreen.instance(Strings.EMPTY, true));
    }

    private void onError (String message) {
        hideProgress();
        ClientUtils.showDialogError(mainActivity, getString(R.string.err), message);
    }

    private void hideProgress () {
        if (progressDialog.isShowing()) {
            progressDialog.hide();
        }
    }

}
