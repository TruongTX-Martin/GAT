package com.gat.feature.login.ForgotPassword;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gat.R;
import com.gat.app.activity.ScreenActivity;
import com.gat.common.util.CommonCheck;
import com.gat.common.util.Strings;
import com.gat.common.util.Views;
import com.gat.data.response.ResponseData;
import com.gat.data.response.ServerResponse;
import com.gat.data.response.impl.VerifyTokenResponseData;
import com.gat.feature.login.LoginPresenter;
import com.gat.feature.login.LoginScreen;
import com.gat.feature.register.update.location.AddLocationActivity;
import com.gat.feature.register.update.location.AddLocationScreen;

import butterknife.BindView;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by ducbtsn on 3/3/17.
 */

public class VerifyResetTokenActivity extends ScreenActivity<LoginScreen, LoginPresenter> {
    @BindView(R.id.text_input)
    EditText tokenText;

    @BindView(R.id.textEmail)
    TextView emailTextView;

    @BindView(R.id.btn_send)
    Button btnVerify;

    @BindView(R.id.imgBack)
    ImageView imgBack;

    @BindView(R.id.imgSave)
    ImageView imgSave;

    @BindView(R.id.txtTitle)
    TextView txtTitle;

    @BindView(R.id.layoutMenutop)
    RelativeLayout headerLayout;

    CompositeDisposable disposable;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedBundleInstance) {
        super.onCreate(savedBundleInstance);

        emailTextView.setText(getScreen().email());

        headerLayout.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.background_header_blue, null));
        txtTitle.setText(getString(R.string.forgot_password));
        txtTitle.setAllCaps(true);
        txtTitle.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorWhite, null));
        imgBack.setImageResource(R.drawable.narrow_back_black);
        imgSave.setVisibility(View.GONE);

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

        imgBack.setOnClickListener(v -> {
            start(getApplicationContext(), ForgotPasswordActivity.class, LoginScreen.instance(Strings.EMPTY));
            this.finish();
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
        this.start(getApplicationContext(), ResetPasswordActivity.class, getScreen());
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
