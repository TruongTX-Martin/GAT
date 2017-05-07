package com.gat.feature.setting.add_email_password;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.EditText;
import android.widget.Toast;

import com.gat.R;
import com.gat.app.fragment.ScreenFragment;
import com.gat.feature.setting.ISettingDelegate;
import com.gat.feature.setting.KeyBackToMain;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by mryit on 5/5/2017.
 */

@SuppressLint("ValidFragment")
public class AddEmailPasswordFragment extends ScreenFragment<AddEmailPasswordScreen, AddEmailPasswordPresenter> {

    @BindView(R.id.edit_text_email)
    EditText editTextEmail;

    @BindView(R.id.edit_text_password)
    EditText editTextPassword;

    private ISettingDelegate delegate;
    private CompositeDisposable disposable;

    public AddEmailPasswordFragment(ISettingDelegate delegate) {
        this.delegate = delegate;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.frame_add_email_password;
    }

    @Override
    protected AddEmailPasswordScreen getDefaultScreen() {
        return AddEmailPasswordScreen.instance();
    }

    @Override
    protected Class<AddEmailPasswordPresenter> getPresenterClass() {
        return AddEmailPasswordPresenter.class;
    }

    @Override
    protected Object getPresenterKey() {
        return AddEmailPasswordScreen.instance();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        disposable = new CompositeDisposable(
                getPresenter().onSuccess().subscribe(this::onAddSuccess),
                getPresenter().onFailed().subscribe(this::onAddFailed),
                getPresenter().onEmailEmpty().subscribe(this::onEmailEmpty),
                getPresenter().onPasswordEmpty().subscribe(this::onPasswordEmpty)
        );
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        disposable.dispose();
    }

    @OnClick(R.id.image_view_back)
    void onBackPress() {
        delegate.goToMainSetting(KeyBackToMain.BACK_BUTTON);
    }

    @OnClick(R.id.image_button_save)
    void onSaveTap () {
        editTextEmail.setError(null);
        editTextPassword.setError(null);

        getPresenter().requestAddEmailPassword(editTextEmail.getText().toString(), editTextPassword.getText().toString());
    }

    void onAddSuccess (String message) {
        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
        delegate.goToMainSetting(KeyBackToMain.ADD_EMAIL_PASSWORD);
    }

    void onAddFailed (String message) {
        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
    }

    void onEmailEmpty (String message) {
        editTextEmail.setError(getString(R.string.err_password_empty));
    }

    void onPasswordEmpty (String message) {
        editTextPassword.setError(getString(R.string.err_password_empty));
    }

}
