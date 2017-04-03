package com.gat.feature.register;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.widget.Button;
import android.widget.EditText;
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
import com.gat.common.util.CommonCheck;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.subjects.PublishSubject;

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

    // For facebook callback
    private CallbackManager callbackManager;

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

        disposables = new CompositeDisposable(
                getPresenter().getResponse().subscribe(this::onRegisterSuccess),
                getPresenter().onError().subscribe(this::onRegisterError)
        );

        registerWithFacebook();

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
            onLogging(true);
            getPresenter().setIdentity(
                    EmailLoginData.instance(email, password, name, Strings.EMPTY, LoginData.Type.EMAIL)
            );
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
                                try {
                                    String name = object.has("sender") ? object.getString("sender") : Strings.EMPTY;
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
                                    onLogging(true);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        //Here we put the requested fields to be returned from the JSONObject
                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "sender, email, picture");
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
        faceRegisterBtn.setOnClickListener(e -> {
            LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile"));
        });
    }
    private void onRegisterError(ServerResponse<ResponseData> error) {
        onLogging(false);
        Toast.makeText(this, error.message(), Toast.LENGTH_SHORT).show();
    }

    private void onRegisterSuccess(User user) {
        onLogging(false);
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
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
