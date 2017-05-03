package com.gat.feature.register.update.info;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.gat.R;
import com.gat.common.util.CommonCheck;
import com.gat.common.util.Strings;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by ducbtsn on 2/28/17.
 */

public class UpdateInfoActivity extends Activity {
    @BindView(R.id.update_btn)
    Button updateBtn;

    @BindView(R.id.update_email)
    EditText email;

    @BindView(R.id.update_password)
    EditText password;

    @BindView(R.id.update_edit_name)
    EditText editName;

    @BindView(R.id.update_text_name)
    TextView textName;

    @BindView(R.id.update_image)
    ImageView avatar;

    private Unbinder unbinder;
    private String nameStr;
    private String emailStr;
    private String passwordStr;
    private String urlStr;

    @Override
    protected void onCreate(Bundle savedBundle) {
        super.onCreate(savedBundle);
        setContentView(R.layout.register_update_info);
        unbinder = ButterKnife.bind(this);

        Intent intent = getIntent();
        nameStr = intent.getStringExtra("sender");
        passwordStr = intent.getStringExtra("password");
        emailStr = intent.getStringExtra("email");
        urlStr = intent.getStringExtra("url");

        email.setText(emailStr);
        password.setText(passwordStr);
        editName.setText(nameStr);
        textName.setText(nameStr);
        // TODO image get and display
        updateBtn.setOnClickListener(e -> {
            // check edit sender field
            String name = editName.getText().toString();
            if (Strings.isNullOrEmpty(name)) {
                editName.setError(getString(R.string.name_field_null));
                editName.setText(nameStr);
                return;
            }
            String mail = email.getText().toString();
            String pass = password.getText().toString();
            if (!Strings.isNullOrEmpty(mail)){
                Pair<Boolean, Integer> result = CommonCheck.validateEmail(mail);
                if (!result.first) {
                    String error;
                    if (result.second == CommonCheck.Error.FIELD_EMPTY) {
                        error = getString(R.string.login_email_empty);
                    } else if (result.second == CommonCheck.Error.EMAIL_INVALID) {
                        error = getString(R.string.login_email_invalid);
                    } else {
                        error = Strings.EMPTY;
                    }
                    email.setError(error);
                    return;
                }
                result = CommonCheck.validatePassword(pass);
                if (!result.first) {
                    String error;
                    if (result.second == CommonCheck.Error.FIELD_EMPTY) {
                        error = getString(R.string.login_password_empty);
                    } else if (result.second == CommonCheck.Error.EMAIL_INVALID) {
                        error = getString(R.string.login_password_length);
                    } else {
                        error = Strings.EMPTY;
                    }
                    password.setError(error);
                    return;
                }
            }
            Intent result = new Intent();
            intent.putExtra("sender", name);
            intent.putExtra("email", Strings.isNullOrEmpty(mail) ? Strings.EMPTY : mail);
            intent.putExtra("url", urlStr);
            intent.putExtra("password", Strings.isNullOrEmpty(pass) ? Strings.EMPTY : pass);
            setResult(RESULT_OK, result);
            finish();
        });

    }


    @Override
    protected void onDestroy() {
        unbinder.unbind();
        super.onDestroy();
    }
}
