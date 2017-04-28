package com.gat.feature.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.gat.R;
import com.gat.app.activity.ScreenActivity;
import com.gat.common.util.CommonUtil;
import com.gat.common.util.Strings;
import com.gat.common.util.Views;
import com.gat.data.response.ResponseData;
import com.gat.data.response.ServerResponse;
import com.gat.data.user.EmailLoginData;
import com.gat.data.user.SocialLoginData;
import com.gat.feature.login.ForgotPassword.ForgotPasswordActivity;
import com.gat.feature.main.MainActivity;
import com.gat.feature.main.MainScreen;
import com.gat.repository.entity.LoginData;
import com.gat.repository.entity.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import butterknife.BindView;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by ducbtsn on 2/22/17.
 */

public class LoginActivity extends ScreenActivity<LoginScreen, LoginPresenter> {
    @BindView(R.id.input_email)
    EditText emailText;

    @BindView(R.id.input_password)
    EditText passwordText;

    @BindView(R.id.text_forgot_password)
    TextView forgotTextView;

    @BindView(R.id.btn_login)
    Button loginBtn;

    @BindView(R.id.face_login_btn)
    ImageButton faceLoginBtn;

    @BindView(R.id.google_login_btn)
    ImageButton googleLoginBtn;

    @BindView(R.id.twitter_login_btn)
    ImageButton twitterLoginBtn;

    private CompositeDisposable disposables;
    private ProgressDialog progressDialog;

    // For facebook login
    private CallbackManager mCallbackManager;

    @Override
    protected int getLayoutResource() {
        return R.layout.login_activity;
    }

    @Override
    protected LoginScreen getDefaultScreen() {
        return LoginScreen.instance(Strings.EMPTY);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        progressDialog = new ProgressDialog(this);

        disposables = new CompositeDisposable(
                getPresenter().loginResult().subscribe(this::onLoginResult),
                getPresenter().onError().subscribe(this::onLoginError),
                getPresenter().loadLocalLoginData().
                        filter(loginData -> loginData != LoginData.EMPTY)
                        .subscribe(loginData -> {
                            onLogging(true);
                            getPresenter().setIdentity(loginData);
                        })
        );

        // login button
        loginBtn.setOnClickListener(view -> {
            String email = emailText.getText().toString();
            String password = passwordText.getText().toString();
            Pair<Boolean, Integer> result = CommonUtil.validateEmail(email);
            if (!result.first) {
                String error;
                if (result.second == CommonUtil.Error.FIELD_EMPTY) {
                    error = getString(R.string.login_email_empty);
                } else if (result.second == CommonUtil.Error.EMAIL_INVALID) {
                    error = getString(R.string.login_email_invalid);
                } else {
                    error = Strings.EMPTY;
                }
                emailText.setError(error);
                return;
            }
            result = CommonUtil.validatePassword(password);
            if (!result.first) {
                String error;
                if (result.second == CommonUtil.Error.FIELD_EMPTY) {
                    error = getString(R.string.login_password_empty);
                } else if (result.second == CommonUtil.Error.EMAIL_INVALID) {
                    error = getString(R.string.login_password_length);
                } else {
                    error = Strings.EMPTY;
                }
                passwordText.setError(error);
                return;
            }
            String name = email.substring(0, email.indexOf('@'));
            onLogging(true);
            getPresenter().setIdentity(
                    EmailLoginData.instance(email, password, name, Strings.EMPTY, LoginData.Type.EMAIL)
            );
        });

        loginWithFacebook();

        // Forgot password page
        forgotTextView.setOnClickListener(view -> {
            start(getApplicationContext(), ForgotPasswordActivity.class, LoginScreen.instance(Strings.EMPTY));
            finish();
        });
    }

    private void onLoginResult(User user) {
        onLogging(false);
        start(this, MainActivity.class, MainScreen.instance());
        finish();
    }

    private void onLoginError(ServerResponse<ResponseData> responseData) {
        onLogging(false);
        Toast.makeText(getApplicationContext(), responseData.message(), Toast.LENGTH_SHORT).show();
    }

    private void loginWithFacebook() {
        mCallbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(mCallbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                try {
                                    String name = object.has("name") ? object.getString("name") : Strings.EMPTY;
                                    String email = object.has("email") ? object.getString("email") : Strings.EMPTY;
                                    String image = object.has("picture") ? object.getJSONObject("picture").getJSONObject("data").getString("url") : Strings.EMPTY;
                                    String userId = loginResult.getAccessToken().getUserId();
                                    String token = loginResult.getAccessToken().getToken();

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
                                    onLogging(true);
                                } catch (JSONException e) {
                                    e.printStackTrace();
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
                    public void onCancel() {
                        Toast.makeText(getApplicationContext(), getString(R.string.login_social_cancel), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        exception.printStackTrace();
                    }
                });
        faceLoginBtn.setOnClickListener(e -> {
            LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile"));
        });
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
        disposables.dispose();
    }

    @Override
    protected Class<LoginPresenter> getPresenterClass() {
        return LoginPresenter.class;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

}
