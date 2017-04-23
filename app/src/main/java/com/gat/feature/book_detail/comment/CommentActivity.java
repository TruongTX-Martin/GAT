package com.gat.feature.book_detail.comment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.gat.R;
import com.gat.app.activity.ScreenActivity;
import com.gat.data.response.impl.EvaluationItemResponse;
import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by mryit on 4/17/2017.
 */

public class CommentActivity extends ScreenActivity<CommentScreen, CommentPresenter>{


    @BindView(R.id.text_view_user_name)
    TextView textViewUserName;

    @BindView(R.id.edit_text_comment)
    EditText editTextComment;

    private CompositeDisposable disposable;
    private EvaluationItemResponse evaluation;

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
        return CommentScreen.instance(getScreen().evaluation());
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        evaluation = getScreen().evaluation();

        disposable = new CompositeDisposable(
                getPresenter().onPostCommentSuccess().subscribe(this::onPostCommentSuccess),
                getPresenter().onPostCommentFailure().subscribe(this::onPostCommentFailure)
        );

    }

    @Override
    protected void onResume() {
        super.onResume();

        textViewUserName.setText(evaluation.getName());
        editTextComment.setText(evaluation.getReview() == null? "" : evaluation.getReview());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposable.dispose();
    }

    @OnClick(R.id.image_view_back)
    void onBack () {
        finish();
    }

    @OnClick(R.id.button_post_comment)
    void onButtonPostCommentTap () {
        getPresenter().postComment(evaluation.getEditionId(), evaluation.getValue(), editTextComment.getText().toString(), false);
    }

    private void onPostCommentSuccess (String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        finish();
    }

    private void onPostCommentFailure (String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

}
