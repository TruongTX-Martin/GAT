package com.gat.feature.book_detail;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.gat.R;
import com.gat.app.activity.ScreenActivity;
import com.gat.common.util.MZDebug;
import com.gat.data.response.UserResponse;
import com.gat.data.response.impl.BookInfo;
import com.gat.data.response.impl.BookReadingInfo;
import com.gat.data.response.impl.EvaluationItemResponse;
import com.gat.feature.book_detail.adapter.EvaluationBuilder;
import com.gat.feature.book_detail.adapter.EvaluationItemAdapter;
import com.gat.feature.book_detail.add_to_bookcase.AddToBookcaseActivity;
import com.gat.feature.book_detail.comment.CommentActivity;
import com.gat.feature.book_detail.list_user_sharing_book.ListUserSharingBookActivity;
import com.gat.feature.book_detail.self_update_reading.SelfUpdateReadingActivity;
import com.gat.feature.book_detail.share_book.ShareBookActivity;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by mryit on 4/16/2017.
 */

public class BookDetailActivity extends ScreenActivity<BookDetailScreen, BookDetailPresenter>{

    @BindView(R.id.scroll_view)
    ScrollView scrollView;

    @BindView(R.id.text_view_book_name)
    TextView textViewBookName;

    @BindView(R.id.text_view_book_author)
    TextView textViewBookAuthor;

    @BindView(R.id.text_view_rating)
    TextView textViewRating;

    @BindView(R.id.rating_bar_book)
    RatingBar ratingBarBook;

    @BindView(R.id.button_reading_state)
    Button buttonReadingState;

    @BindView(R.id.rating_bar_user_rate)
    RatingBar ratingBarUserRate;

    @BindView(R.id.text_view_comment_by_user)
    TextView textViewCommentByUser;

    @BindView(R.id.button_comment)
    Button buttonComment;

    @BindView(R.id.text_view_book_description)
    TextView textViewBookDescription;

    @BindView(R.id.text_view_sharing_book)
    TextView textViewSharingBook;

    @BindView(R.id.recycler_view_comments)
    RecyclerView recyclerView;

    private CompositeDisposable disposables;
    EvaluationItemAdapter adapter;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_book_detail;
    }

    @Override
    protected Class<BookDetailPresenter> getPresenterClass() {
        return BookDetailPresenter.class;
    }

    @Override
    protected Object getPresenterKey() {
        return BookDetailScreen.instance(getScreen().editionId());
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getPresenter().setEditionId(getScreen().editionId());
        setupRecyclerViewComments();

        disposables = new CompositeDisposable(
                getPresenter().onGetBookInfoSuccess().subscribe(this::onGetBookInfoSuccess),
                getPresenter().onGetEditionSharingUsersSuccess().subscribe(this::onGetEditionSharingUsersSuccess),
                getPresenter().onGetBookEvaluationByUser().subscribe(this::onGetBookEvaluationByUser),
                getPresenter().onGetSelfReadingStatusSuccess().subscribe(this::onGetSelfReadingStatusSuccess),
                getPresenter().onGetBookEditionEvaluationSuccess().subscribe(this::onGetBookEditionEvaluationSuccess),
                getPresenter().onError().subscribe(this::onError),
                getPresenter().onUserNotLoggedIn().subscribe(this::onUserNotLoggedIn)
        );

        getPresenter().getBookInfo();
        getPresenter().getEditionSharingUsers();
        getPresenter().getBookEvaluationByUser();
        getPresenter().getSelfReadingStatus();
        getPresenter().getBookEditionEvaluation();

    }

    @Override
    protected void onStart() {
        super.onStart();
        scrollView.smoothScrollBy(0,0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @OnClick(R.id.image_button_back)
    void onBack () {
        finish();
    }

    @OnClick(R.id.image_button_go_share)
    void onGoShare () {
        Intent intent = new Intent(getApplicationContext(), ShareBookActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.button_reading_state)
    void updateReadingState () {

        Intent intent = new Intent(getApplicationContext(), SelfUpdateReadingActivity.class);
        startActivity(intent);

        // nếu readingStatus = -1 sẽ hiển thị là Muốn đọc
        // nhấn vào thì sẽ hiện dấu tích bên trái và gọi api update status;

        // nếu readingStatus >=0 thì hiển thị tên tương ứng
        // + 0: Readed - đã đọc
        // + 1: Reading - đang đọc
        // + 2: To read - sẽ đọc
        // khi nhấp vào thì sẽ gọi SelfUpdateReadingActivity

    }

    @OnClick(R.id.button_comment)
    void onGoComment () {
        Intent intent = new Intent(getApplicationContext(), CommentActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.image_button_plus)
    void onAddToBookCase () {
        Intent intent = new Intent(getApplicationContext(), AddToBookcaseActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.button_view_list)
    void onViewListSharingThisBook () {
        Intent intent = new Intent(getApplicationContext(), ListUserSharingBookActivity.class);
        startActivity(intent);
    }

    private void setupRecyclerViewComments () {
        adapter = new EvaluationItemAdapter();
        this.recyclerView.setHasFixedSize(true);
        this.recyclerView.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        this.recyclerView.setNestedScrollingEnabled(false);
        this.recyclerView.setAdapter(adapter);
    }

    private void onGetBookInfoSuccess (BookInfo book) {
        MZDebug.w(" onGetBookInfoSuccess: " + book.toString());

        textViewBookName.setText(book.getTitle());
        textViewBookAuthor.setText(book.getAuthor().get(0).getAuthorName());
        textViewRating.setText(String.valueOf(book.getRateAvg()));
        ratingBarBook.setRating(book.getRateAvg());
        textViewSharingBook.setText(String.valueOf(book.getSharingCount()));
        textViewBookDescription.setText(book.getDescription());
    }

    private List<EvaluationItemResponse> listEvaluations;
    private void onGetBookEditionEvaluationSuccess (List<EvaluationItemResponse> list) {
        listEvaluations = list;
        adapter.setItem(EvaluationBuilder.transformListEvaluation(list));
        MZDebug.w("onGetBookEditionEvaluationSuccess: " + list.size());
    }

    private BookReadingInfo mBookReadingInfo;
    private void onGetSelfReadingStatusSuccess (BookReadingInfo bookReadingInfo) {
        mBookReadingInfo = bookReadingInfo;
        MZDebug.w("onGetSelfReadingStatusSuccess: " + bookReadingInfo.toString());
    }

    private List<UserResponse> mListUserSharing;
    private void onGetEditionSharingUsersSuccess (List<UserResponse> list) {
        mListUserSharing = list;
        MZDebug.w("onGetEditionSharingUsersSuccess: " + list.size());
    }

    private EvaluationItemResponse mEvaluationByUser;
    private void onGetBookEvaluationByUser (EvaluationItemResponse evaluation) {
        mEvaluationByUser = evaluation;
        MZDebug.w("onGetEditionSharingUsersSuccess: " + evaluation.toString());

        textViewCommentByUser.setVisibility(View.VISIBLE);
        buttonComment.setVisibility(View.VISIBLE);
        textViewCommentByUser.setText(evaluation.getReview());
    }

    private void onError (String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void onUserNotLoggedIn (String message) {
        // hide button comment, string comment
        textViewCommentByUser.setVisibility(View.GONE);
        buttonComment.setVisibility(View.GONE);
    }

}
