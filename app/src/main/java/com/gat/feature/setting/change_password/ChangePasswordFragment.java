package com.gat.feature.setting.change_password;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.gat.R;
import com.gat.app.fragment.ScreenFragment;
import com.gat.common.util.ClientUtils;
import com.gat.common.util.Strings;
import com.gat.common.util.Views;
import com.gat.feature.login.ForgotPassword.ForgotPasswordActivity;
import com.gat.feature.login.LoginScreen;
import com.gat.feature.main.MainActivity;
import com.gat.feature.setting.ISettingDelegate;
import com.gat.feature.setting.KeyBackToMain;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by mozaa on 05/05/2017.
 */

@SuppressLint("ValidFragment")
public class ChangePasswordFragment extends ScreenFragment<ChangePasswordScreen, ChangePasswordPresenter>
implements TextWatcher{

    @BindView(R.id.edit_text_current_password)
    EditText editTextCurrentPassword;

    @BindView(R.id.edit_text_new_password)
    EditText editTextNewPassword;

    @BindView(R.id.edit_text_confirm_password)
    EditText editTextConfirmPassword;

    @BindView(R.id.image_button_save)
    ImageView imageViewSave;

    private ISettingDelegate delegate;
    private CompositeDisposable disposable;
    private AlertDialog progressDialog;

    public ChangePasswordFragment (ISettingDelegate delegate) {
        this.delegate = delegate;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.frame_setting_change_password;
    }

    @Override
    protected Object getPresenterKey() {
        return ChangePasswordScreen.instance();
    }

    @Override
    protected Class<ChangePasswordPresenter> getPresenterClass() {
        return ChangePasswordPresenter.class;
    }

    @Override
    protected ChangePasswordScreen getDefaultScreen() {
        return ChangePasswordScreen.instance();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        disposable = new CompositeDisposable(
                getPresenter().onChangePasswordSuccess().subscribe(this::onChangePasswordSuccess),
                getPresenter().onChangePasswordFailed().subscribe(this::onChangePasswordFailed),
                getPresenter().onUnAuthorization().subscribe(this::onUnAuthorization),
                getPresenter().onShowOrHideProgress().subscribe(this::onWhichShowOrHideProgress)
        );
        progressDialog = ClientUtils.createLoadingDialog(getActivity());

        editTextCurrentPassword.addTextChangedListener(this);
        editTextNewPassword.addTextChangedListener(this);
        editTextConfirmPassword.addTextChangedListener(this);
    }

    @Override
    public void onDestroy() {
        disposable.dispose();
        hideProgress();
        progressDialog = null;
        super.onDestroy();
    }


    @OnClick(R.id.image_view_back)
    void onBackPress() {
        if ( ! TextUtils.isEmpty(editTextCurrentPassword.getText().toString()) ||
             ! TextUtils.isEmpty(editTextNewPassword.getText().toString()) ||
             ! TextUtils.isEmpty(editTextConfirmPassword.getText().toString())) {

            ClientUtils.showChangedValueDialog(getActivity());
            return;
        }

        Views.hideKeyboard(getActivity());
        delegate.goToMainSetting(KeyBackToMain.BACK_BUTTON);
    }


    @OnClick(R.id.image_button_save)
    void onSaveTap () {
        getPresenter().requestChangePassword(
                editTextCurrentPassword.getText().toString(),
                editTextNewPassword.getText().toString(),
                editTextConfirmPassword.getText().toString());
    }

    @OnClick(R.id.button_forgot_password)
    void onButtonForgotPasswordTap () {
        MainActivity.start(getActivity().getApplicationContext(),
                ForgotPasswordActivity.class, LoginScreen.instance(Strings.EMPTY));
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if ( ! TextUtils.isEmpty(editTextCurrentPassword.getText().toString()) ||
                ! TextUtils.isEmpty(editTextNewPassword.getText().toString()) ||
                ! TextUtils.isEmpty(editTextConfirmPassword.getText().toString())) {

            imageViewSave.setClickable(true);
            imageViewSave.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_check_green, null));
        } else {
            imageViewSave.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_check_gray, null));
            imageViewSave.setClickable(false);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
    }

    void onChangePasswordSuccess (String message) {

        Views.hideKeyboard(getActivity());
        delegate.goToMainSetting(KeyBackToMain.BACK_CHANGE_PASSWORD);
    }

    void onChangePasswordFailed (String message) {
        ClientUtils.showDialogError(getActivity(), getString(R.string.err), message);
    }

    void onUnAuthorization (String message) {
        ClientUtils.showDialogUnAuthorization(getActivity(), (MainActivity) getActivity(), message);
    }

    void onWhichShowOrHideProgress (Boolean isShow) {
        Views.hideKeyboard(getActivity());

        if (isShow) {
            progressDialog.show();
        } else {
            hideProgress();
        }
    }

    void hideProgress () {
        if (progressDialog.isShowing()) {
            progressDialog.hide();
        }
    }
}
