package com.gat.feature.book_detail.comment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.gat.R;
import com.gat.app.GatApplication;
import com.gat.app.activity.ScreenActivity;
import com.gat.common.listener.OnNetworkChangeListener;
import com.gat.common.util.ClientUtils;
import com.gat.common.util.MZDebug;
import com.gat.data.response.impl.EvaluationItemResponse;
import com.gat.feature.book_detail.BookDetailActivity;
import com.gat.feature.main.MainActivity;
import com.gat.repository.entity.User;

import java.util.Observable;
import java.util.Observer;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by mryit on 4/17/2017.
 */

public class CommentActivity extends ScreenActivity<CommentScreen, CommentPresenter> implements Observer{


    @BindView(R.id.text_view_user_name)
    TextView textViewUserName;

    @BindView(R.id.edit_text_comment)
    EditText editTextComment;

    private CompositeDisposable disposable;

    AlertDialog errorDialog;
    AlertDialog changedValueDialog;
    AlertDialog loadingDialog;
    AlertDialog unAuthorizationDialog;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_comment;
    }

    @Override
    protected Class<CommentPresenter> getPresenterClass() {
        return CommentPresenter.class;
    }

    @Override
    protected Object getPresenterKey() {
        return CommentScreen.instance(getScreen().editionId(), getScreen().value(), getScreen().comment());
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        disposable = new CompositeDisposable(
                getPresenter().onPostCommentSuccess().subscribe(this::onPostCommentSuccess),
                getPresenter().onPostCommentFailure().subscribe(this::onPostCommentFailure),
                getPresenter().onLoadUserCachedSuccess().subscribe(this::onLoadUserCacheSuccess),
                getPresenter().onUnAuthorization().subscribe(this::onUnAuthorization)
        );

        gatApplication = (GatApplication) getApplicationContext();
        gatApplication.getObserverNetworkChange().addObserver(this);

        loadingDialog = ClientUtils.createLoadingDialog(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        getPresenter().loadUserCached();
        editTextComment.setText(TextUtils.isEmpty(getScreen().comment()) ? "" : getScreen().comment());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposable.dispose();

        if (errorDialog != null) {
            errorDialog.dismiss();
        }
        if (changedValueDialog != null) {
            changedValueDialog.dismiss();
        }
        if (loadingDialog != null) {
            loadingDialog.dismiss();
        }
        if (unAuthorizationDialog != null) {
            unAuthorizationDialog.dismiss();
        }
    }

    @Override
    public void onBackPressed() {
        if (TextUtils.equals(getScreen().comment(), editTextComment.getText().toString())) {
            super.onBackPressed();
        } else {
            changedValueDialog = ClientUtils.showChangedValueDialog(this);
        }
    }

    @OnClick(R.id.image_view_back)
    void onBack () {
        if (TextUtils.equals(getScreen().comment(), editTextComment.getText().toString())) {
            super.onBackPressed();
        } else {
            changedValueDialog = ClientUtils.showChangedValueDialog(this);
        }
    }

    @OnClick(R.id.button_post_comment)
    void onButtonPostCommentTap () {
        showProgress();
        getPresenter().postComment(getScreen().editionId(), getScreen().value(), editTextComment.getText().toString(), false);
    }

    private void onPostCommentSuccess (String message) {
        hideProgress();
        Intent returnIntent = new Intent();
        returnIntent.putExtra(BookDetailActivity.KEY_UPDATE_COMMENT, editTextComment.getText().toString());
        setResult(RESULT_OK, returnIntent);
        finish();
    }

    private void onPostCommentFailure (String message) {
        hideProgress();
        errorDialog = ClientUtils.showErrorDialog(getString(R.string.err), message, this);
    }

    private void onLoadUserCacheSuccess (User user) {
        textViewUserName.setText(user.name());
    }

    private void showProgress () {
        loadingDialog.show();
    }

    private void hideProgress () {
        if (loadingDialog.isShowing()) {
            loadingDialog.hide();
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        Toast.makeText(this, " Network is online = " + ((OnNetworkChangeListener)o).isOnline(), Toast.LENGTH_SHORT).show();

        if ( ! ((OnNetworkChangeListener)o).isOnline()) {
            // khong online, show dialog
        }
    }

    private void onUnAuthorization (String message) {
        unAuthorizationDialog = ClientUtils.showDialogUnAuthorization(this, this, message);
    }

}
