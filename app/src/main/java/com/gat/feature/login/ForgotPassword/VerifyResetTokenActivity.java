package com.gat.feature.login.ForgotPassword;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.gat.R;
import com.gat.app.activity.ScreenActivity;
import com.gat.common.util.CommonCheck;
import com.gat.common.util.Strings;
import com.gat.common.util.Views;
import com.gat.data.response.ResponseData;
import com.gat.data.response.ServerResponse;
import com.gat.data.response.impl.ResetPasswordResponseData;
import com.gat.data.response.impl.VerifyTokenResponseData;
import com.gat.domain.usecase.ResetPassword;
import com.gat.feature.login.LoginPresenter;
import com.gat.feature.login.LoginScreen;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * Created by ducbtsn on 3/3/17.
 */

public class VerifyResetTokenActivity extends ScreenActivity<LoginScreen, LoginPresenter> {
    @BindView(R.id.input_token)
    EditText tokenText;

    @BindView(R.id.textEmail)
    TextView emailTextView;

    @BindView(R.id.btn_send)
    Button btnVerify;

    CompositeDisposable disposable;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedBundleInstance) {
        super.onCreate(savedBundleInstance);

        emailTextView.setText(getScreen().email());

        disposable = new CompositeDisposable(
                getPresenter().verifyResult().subscribe(this::onSuccess),
                getPresenter().onError().subscribe(this::onError)
        );

        getPresenter().init();

        progressDialog =  new ProgressDialog(this);

        btnVerify.setOnClickListener(view -> {
            String token = tokenText.getText().toString();
            if (!CommonCheck.checkToken(token)) {
                tokenText.setError(getString(R.string.forgot_inputtoken_length));
                return;
            }
            onVerifying(true);
            getPresenter().verifyToken(token);
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposable.dispose();
    }

    @Override
    protected LoginScreen getDefaultScreen() {
        return LoginScreen.instance(Strings.EMPTY);
    }

    public void onError(String error) {
        onVerifying(false);
        Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT).show();
    }

    public void onSuccess(ServerResponse<VerifyTokenResponseData> responseDataServerResponse) {
        onVerifying(false);
        this.start(getApplicationContext(), ResetPasswordActivity.class, LoginScreen.instance(Strings.EMPTY));
        finish();
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.verify_token_activity;
    }

    @Override
    protected Class<LoginPresenter> getPresenterClass() {
        return LoginPresenter.class;
    }

    private void onVerifying(boolean enter) {
        if (enter) {
            Views.hideKeyboard(this);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage(getString(R.string.verifying));
            progressDialog.show();
        } else {
            progressDialog.dismiss();
        }
    }
}
