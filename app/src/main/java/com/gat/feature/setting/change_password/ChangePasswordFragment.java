package com.gat.feature.setting.change_password;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.gat.R;
import com.gat.app.fragment.ScreenFragment;
import com.gat.common.util.Strings;
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
public class ChangePasswordFragment extends ScreenFragment<ChangePasswordScreen, ChangePasswordPresenter> {

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
                getPresenter().onChangePasswordFailed().subscribe(this::onChangePasswordFailed)
        );
    }

    @Override
    public void onDestroy() {
        disposable.dispose();
        super.onDestroy();
    }

    @OnClick(R.id.image_view_back)
    void onBackPress() {
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

    void onChangePasswordSuccess (String message) {
        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();

        // back to main setting fragment
        delegate.goToMainSetting(KeyBackToMain.BACK_CHANGE_PASSWORD);
    }

    void onChangePasswordFailed (String message) {
        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
    }

}
