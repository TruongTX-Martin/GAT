package com.gat.feature.register;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookAuthorizationException;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.gat.R;
import com.gat.app.activity.ScreenActivity;
import com.gat.common.util.ClientUtils;
import com.gat.common.util.CommonCheck;
import com.gat.common.util.Constance;
import com.gat.common.util.Strings;
import com.gat.common.util.Views;
import com.gat.data.response.ResponseData;
import com.gat.data.response.ServerResponse;
import com.gat.data.response.impl.LoginResponseData;
import com.gat.data.user.EmailLoginData;
import com.gat.data.user.SocialLoginData;
import com.gat.feature.register.update.category.AddCategoryActivity;
import com.gat.feature.register.update.info.UpdateInfoActivity;
import com.gat.feature.register.update.location.AddLocationActivity;
import com.gat.feature.register.update.location.AddLocationScreen;
import com.gat.repository.entity.LoginData;
import com.gat.repository.entity.User;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;
import com.twitter.sdk.android.core.services.AccountService;

import io.fabric.sdk.android.Fabric;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

/**
 * Created by ducbtsn on 2/26/17.
 */

public class RegisterActivity extends ScreenActivity<RegisterScreen, RegisterPresenter> {
    private CompositeDisposable disposables;
    private ProgressDialog progressDialog;
    private Unbinder unbinder;

    @BindView(R.id.input_email)
    EditText emailText;

    @BindView(R.id.input_password)
    EditText passwordText;

    @BindView(R.id.btn_register)
    Button registerBtn;

    @BindView(R.id.face_register_btn)
    Button faceRegisterBtn;

    @BindView(R.id.twitter_register_btn)
    Button twitterRegisterBtn;

    @BindView(R.id.google_register_btn)
    Button googleRegisterBtn;

    private Subject<Boolean> progressSubject;

    // For facebook callback
    private CallbackManager callbackManager;

    // For register with google account
    private GoogleApiClient googleApiClient;

    // For register with twitter account
    private TwitterAuthClient twitterAuthClient;

    @Override
    protected RegisterScreen getDefaultScreen() {
        return RegisterScreen.instance();
    }
    @Override
    protected int getLayoutResource() {
        return R.layout.register_activity;
    }

    @Override
    protected Class<RegisterPresenter> getPresenterClass() {
        return RegisterPresenter.class;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        unbinder = ButterKnife.bind(this);

        progressDialog =  new ProgressDialog(this);

        progressSubject = BehaviorSubject.create();

        disposables = new CompositeDisposable(
                getPresenter().getResponse().subscribe(this::onRegisterSuccess),
                getPresenter().onError().subscribe(this::onRegisterError),
                progressSubject.subscribe(this::onLogging)
        );

        registerWithFacebook();

        registerWithGoogle();

        registerWithTwitter();

        registerWithEmail();

    }

    private void registerWithTwitter() {
        ApplicationInfo app = null;
        try {
            app = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = app.metaData;
            String apiKey = bundle.getString("io.fabric.ApiKey");
            String apiSecret = getString(R.string.twitter_api_secret);
            TwitterAuthConfig authConfig = new TwitterAuthConfig(
                    apiKey,
                    apiSecret);
            Fabric.with(this, new Twitter(authConfig));
            twitterAuthClient = new TwitterAuthClient();
            twitterRegisterBtn.setOnClickListener(view -> {
                twitterAuthClient.authorize(this, new Callback<TwitterSession>() {
                    @Override
                    public void success(Result<TwitterSession> resultSession) {
                        TwitterSession session = resultSession.data;
                        AccountService service = Twitter.getApiClient(session).getAccountService();
                        service.verifyCredentials(true, true).enqueue(new Callback<com.twitter.sdk.android.core.models.User>() {
                            @Override
                            public void success(Result<com.twitter.sdk.android.core.models.User> resultUser) {
                                String name = resultUser.data.name;
                                String email = resultUser.data.email != null ? resultUser.data.email : Strings.EMPTY;
                                String image = resultUser.data.profileImageUrl != null ? resultUser.data.profileImageUrl : Strings.EMPTY;
                                String userId = Long.toString(resultUser.data.getId());
                                String token = resultSession.data.getAuthToken().token;
                                String secret = resultSession.data.getAuthToken().secret;
                                progressSubject.onNext(true);
                                // Logging
                                getPresenter().setIdentity(SocialLoginData.instance(
                                        userId,
                                        LoginData.Type.TWITTER,
                                        email,
                                        Strings.EMPTY,
                                        name,
                                        image,
                                        token,
                                        secret
                                ));
                            }

                            @Override
                            public void failure(TwitterException exception) {
                                exception.printStackTrace();
                                Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void failure(TwitterException exception) {
                        exception.printStackTrace();
                        Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            });
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void registerWithEmail() {
        // register button
        registerBtn.setOnClickListener(e -> {
            String email = emailText.getText().toString();
            String password = passwordText.getText().toString();
            Pair<Boolean, Integer> result = CommonCheck.validateEmail(email);
            if (!result.first) {
                String error;
                if (result.second == CommonCheck.Error.FIELD_EMPTY) {
                    error = getString(R.string.login_email_empty);
                } else if (result.second == CommonCheck.Error.EMAIL_INVALID) {
                    error = getString(R.string.login_email_invalid);
                } else {
                    error = Strings.EMPTY;
                }
                emailText.setError(error);
                return;
            }
            result = CommonCheck.validatePassword(password);
            if (!result.first) {
                String error;
                if (result.second == CommonCheck.Error.FIELD_EMPTY) {
                    error = getString(R.string.login_password_empty);
                } else if (result.second == CommonCheck.Error.EMAIL_INVALID) {
                    error = getString(R.string.login_password_length);
                } else {
                    error = Strings.EMPTY;
                }
                passwordText.setError(error);
                return;
            }
            // Get sender from head of email
            String name = email.substring(0, email.indexOf('@'));
            progressSubject.onNext(true);
            getPresenter().setIdentity(
                    EmailLoginData.instance(email, password, name, Strings.EMPTY, LoginData.Type.EMAIL)
            );
        });
    }
    private void registerWithGoogle() {

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestProfile()
                .build();
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        googleRegisterBtn.setOnClickListener(view -> {
            Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
            startActivityForResult(signInIntent, Constance.RC_SIGN_IN);
        });

    }

    private void registerWithFacebook() {
        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                if (response.getError() != null) {
                                    Log.d("FaceBookRegisterError", response.getError().getErrorMessage());
                                } else {
                                    try {
                                        String name = object.has("name") ? object.getString("name") : Strings.EMPTY;
                                        String email = object.has("email") ? object.getString("email") : Strings.EMPTY;
                                        String image = object.has("picture") ? object.getJSONObject("picture").getJSONObject("data").getString("url") : Strings.EMPTY;
                                        String userId = loginResult.getAccessToken().getUserId();
                                        String token = loginResult.getAccessToken().getToken();
                                        // Logging
                                        getPresenter().setIdentity(SocialLoginData.instance(
                                                userId,
                                                LoginData.Type.FACE,
                                                email,
                                                Strings.EMPTY,
                                                name,
                                                image,
                                                token
                                        ));
                                        // Loading
                                        progressSubject.onNext(true);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        });
                        //Here we put the requested fields to be returned from the JSONObject
                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "name, email, picture");
                        request.setParameters(parameters);
                        request.executeAsync();

                    }
                    @Override
                    public void onError(FacebookException e) {
                        if (e instanceof FacebookAuthorizationException) {
                            if (AccessToken.getCurrentAccessToken() != null) {
                                LoginManager.getInstance().logOut();
                            }
                        }
                    }
                    @Override
                    public void onCancel() {
                        Toast.makeText(getApplicationContext(), getString(R.string.login_social_cancel), Toast.LENGTH_SHORT).show();
                    }
                });
        faceRegisterBtn.setOnClickListener(e -> {
            LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile"));
        });
    }
    private void onRegisterError(String error) {
        progressSubject.onNext(false);
        ClientUtils.showErrorDialog(getString(R.string.register_error_header), error, this);
    }

    private void onRegisterSuccess(User user) {
        progressSubject.onNext(false);
        this.start(getApplicationContext(), AddLocationActivity.class, AddLocationScreen.instance());
        finish();
    }

    private void onLogging(boolean enter) {
        if (enter) {
            Views.hideKeyboard(this);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage(getString(R.string.authenticating));
            progressDialog.show();
        } else {
            progressDialog.dismiss();
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
        disposables.dispose();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == Constance.RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                GoogleSignInAccount acct = result.getSignInAccount();
                String name = acct.getDisplayName();
                String email = acct.getEmail() != null ? acct.getEmail() : Strings.EMPTY;
                Uri uri = acct.getPhotoUrl();
                String image = uri != null ? uri.toString() : Strings.EMPTY;
                String userId = acct.getId();
                String token = acct.getIdToken();
                progressSubject.onNext(true);
                // Logging
                getPresenter().setIdentity(SocialLoginData.instance(
                        userId,
                        LoginData.Type.GOOGLE,
                        email,
                        Strings.EMPTY,
                        name,
                        image,
                        token
                ));
            } else {
                Toast.makeText(getApplicationContext(), "Cannot SignIn.", Toast.LENGTH_SHORT).show();
            }
        } else {
            callbackManager.onActivityResult(requestCode, resultCode, data);
            twitterAuthClient.onActivityResult(requestCode, resultCode, data);
        }
    }
}
