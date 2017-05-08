package com.gat.feature.login.ForgotPassword;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Pair;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.gat.R;
import com.gat.app.activity.ScreenActivity;
import com.gat.common.util.CommonCheck;
import com.gat.common.util.Strings;
import com.gat.common.util.Views;
import com.gat.data.response.ResponseData;
import com.gat.data.response.ServerResponse;
import com.gat.data.response.impl.ResetPasswordResponseData;
import com.gat.feature.login.LoginPresenter;
import com.gat.feature.login.LoginScreen;

import butterknife.BindView;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by ducbtsn on 2/26/17.
 */

public class ForgotPasswordActivity extends ScreenActivity<LoginScreen, LoginPresenter>{

    @BindView(R.id.input_email)
    EditText emailText;
    @BindView(R.id.btn_send)
    Button btnSend;

    CompositeDisposable disposable;

    private ProgressDialog progressDialog;

    @Override
    protected int getLayoutResource() {
        return R.layout.forgot_pass_activity;
    }

    @Override
    protected void onCreate(Bundle savedBundleInstance) {
        super.onCreate(savedBundleInstance);

        disposable = new CompositeDisposable(
                getPresenter().resetPasswordResponse().subscribe(this::onSuccess),
                getPresenter().onError().subscribe(this::onError)
        );

        progressDialog =  new ProgressDialog(this);

        btnSend.setOnClickListener(view -> {
            String email = emailText.getText().toString();
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
            onSending(true);
            getPresenter().sendRequestReset(email);
        });
    }

    public void onError(String error) {
        onSending(false);
        Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT).show();
    }

    public void onSuccess(ServerResponse<ResetPasswordResponseData> responseDataServerResponse) {
        onSending(false);
        this.start(getApplicationContext(), VerifyResetTokenActivity.class, LoginScreen.instance(emailText.getText().toString()));
        finish();
    }

    @Override
    protected Class<LoginPresenter> getPresenterClass() {
        return LoginPresenter.class;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposable.dispose();
    }

    private void onSending(boolean enter) {
        if (enter) {
            Views.hideKeyboard(this);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage(getString(R.string.sending));
            progressDialog.show();
        } else {
            progressDialog.dismiss();
        }
    }
}
