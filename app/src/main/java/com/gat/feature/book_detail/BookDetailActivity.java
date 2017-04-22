package com.gat.feature.book_detail;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.res.ResourcesCompat;
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

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.gat.R;
import com.gat.app.activity.ScreenActivity;
import com.gat.common.util.ClientUtils;
import com.gat.common.util.MZDebug;
import com.gat.data.response.UserResponse;
import com.gat.data.response.impl.BookInfo;
import com.gat.data.response.impl.BookReadingInfo;
import com.gat.data.response.impl.EvaluationItemResponse;
import com.gat.feature.book_detail.adapter.EvaluationBuilder;
import com.gat.feature.book_detail.adapter.EvaluationItemAdapter;
import com.gat.feature.book_detail.add_to_bookcase.AddToBookcaseActivity;
import com.gat.feature.book_detail.add_to_bookcase.AddToBookcaseScreen;
import com.gat.feature.book_detail.comment.CommentActivity;
import com.gat.feature.book_detail.list_user_sharing_book.ListUserSharingBookActivity;
import com.gat.feature.book_detail.list_user_sharing_book.ListUserSharingBookScreen;
import com.gat.feature.book_detail.self_update_reading.ReadingState;
import com.gat.feature.book_detail.self_update_reading.SelfUpdateReadingActivity;
import com.gat.feature.book_detail.self_update_reading.SelfUpdateReadingScreen;
import com.gat.feature.book_detail.share_book.ShareBookActivity;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by mryit on 4/16/2017.
 */

public class BookDetailActivity extends ScreenActivity<BookDetailScreen, BookDetailPresenter>{

    private static final int RC_UPDATE_READING_STATUS = 0x01;
    public static final String KEY_UPDATE_READING_STATUS = "status";

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
    protected BookDetailScreen getDefaultScreen() {
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
                getPresenter().onUserNotLoggedIn().subscribe(this::onUserNotLoggedIn),
                getPresenter().onUpdateReadingStatusSuccess().subscribe(this::onUpdateReadingStatusSuccess)
        );

        getPresenter().getBookInfo();
        getPresenter().getEditionSharingUsers();
        getPresenter().getBookEvaluationByUser();
        getPresenter().getSelfReadingStatus();
        getPresenter().getBookEditionEvaluation();

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        scrollView.smoothScrollBy(0,0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposables.dispose();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_UPDATE_READING_STATUS && resultCode == RESULT_OK) {
            MZDebug.w("________________________ BookDetailActivity : onActivityResult : RESULT OK");
            int statusResult = data.getIntExtra(KEY_UPDATE_READING_STATUS, ReadingState.REMOVE);
            mBookReadingInfo.setReadingStatus(statusResult);
            updateButtonReadingStatus(statusResult);
        }

        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @OnClick(R.id.image_button_back)
    void onBack () {
        finish();
    }

    @OnClick(R.id.image_button_go_share)
    void onGoShare () {
        showFacebookShareDialog();
    }

    @OnClick(R.id.button_reading_state)
    void updateReadingState () {

        if (null == mBookReadingInfo) {
            return;
        }
        // nếu readingStatus = -1 sẽ hiển thị là Muốn đọc
        // nhấn vào thì sẽ hiện dấu tích bên trái và gọi api update status;

        // nếu readingStatus >=0 thì hiển thị tên tương ứng
        // + 0: Readed - đã đọc
        // + 1: Reading - đang đọc
        // + 2: To read - sẽ đọc
        // khi nhấp vào thì sẽ gọi SelfUpdateReadingActivity

        MZDebug.w("Book reading info: " + mBookReadingInfo.toString());
        if (mBookReadingInfo.getReadingStatus() == ReadingState.REMOVE) {
            getPresenter().updateReadingStatus();
        } else {
            startForResult(BookDetailActivity.this, SelfUpdateReadingActivity.class, SelfUpdateReadingScreen.instance(mBookReadingInfo),RC_UPDATE_READING_STATUS);
        }
    }

    @OnClick(R.id.button_comment)
    void onGoComment () {
        Intent intent = new Intent(getApplicationContext(), CommentActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.image_button_plus)
    void onAddToBookCase () {
        if (null == mBookInfo) {
            return;
        }
        start(this, AddToBookcaseActivity.class, AddToBookcaseScreen.instance(mBookInfo));
    }

    @OnClick(R.id.button_view_list)
    void onViewListSharingThisBook () {
        if (mListUserSharing == null) {
            return;
        }

        MZDebug.e("++++++++++++++++++++++++++++ START activity ++++++++++++++++++++++++++++++++++");
        start(this, ListUserSharingBookActivity.class, ListUserSharingBookScreen.instance(mListUserSharing));
    }

    private void updateButtonReadingStatus (int status) {
        MZDebug.w("__________ BookDetailActivity : updateButtonReadingStatus : status = " + status);
        switch (status) {
            case ReadingState.REMOVE:
                buttonReadingState.setText(getResources().getString(R.string.want_to_read));
                buttonReadingState.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.arrow_down_white, 0);
                break;

            case ReadingState.RED:
                buttonReadingState.setText(getResources().getString(R.string.red_book));
                break;

            case ReadingState.READING:
                buttonReadingState.setText(getResources().getString(R.string.reading_book));
                break;

            case ReadingState.TO_READ:
                buttonReadingState.setText(getResources().getString(R.string.want_to_read));
                buttonReadingState.setCompoundDrawablesWithIntrinsicBounds( R.drawable.ic_check_yellow, 0, 0, 0);
                break;
        }
    }

    private void setupRecyclerViewComments () {
        adapter = new EvaluationItemAdapter();
        this.recyclerView.setHasFixedSize(true);
        this.recyclerView.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        this.recyclerView.setNestedScrollingEnabled(false);
        this.recyclerView.setAdapter(adapter);
    }

    private BookInfo mBookInfo;
    private void onGetBookInfoSuccess (BookInfo book) {
        mBookInfo = book;
        MZDebug.w(" onGetBookInfoSuccess: " + book.toString());
        textViewBookName.setText(book.getTitle());
        textViewBookAuthor.setText(book.getAuthor().get(0).getAuthorName());
        textViewRating.setText(String.valueOf(book.getRateAvg()));
        ratingBarBook.setRating(book.getRateAvg());
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

        updateButtonReadingStatus(mBookReadingInfo.getReadingStatus());
    }

    private List<UserResponse> mListUserSharing;
    private void onGetEditionSharingUsersSuccess (List<UserResponse> list) {
        MZDebug.w("onGetEditionSharingUsersSuccess: " + list.size());

        mListUserSharing = list;
        textViewSharingBook.setText(String.valueOf(list.size()));
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

    private void onUpdateReadingStatusSuccess (String message) {
        mBookReadingInfo.setReadingStatus(ReadingState.TO_READ);
        buttonReadingState.setCompoundDrawablesWithIntrinsicBounds( R.drawable.ic_check_yellow, 0, 0, 0);
    }


    private static CallbackManager callbackManager;
    private void showFacebookShareDialog () {
        callbackManager = CallbackManager.Factory.create();
        ShareDialog shareDialog = new ShareDialog(this);
        shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
            @Override
            public void onSuccess(Sharer.Result result) {
                MZDebug.d(BookDetailActivity.class.getSimpleName(), "shared successfully");
                //add your code to handle successful sharing
                Toast.makeText(BookDetailActivity.this, "share success", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel() {
                MZDebug.d(BookDetailActivity.class.getSimpleName(), "sharing cancelled");
                //add your code to handle cancelled sharing
                Toast.makeText(BookDetailActivity.this, "share cancel", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onError(FacebookException error) {
                MZDebug.d(BookDetailActivity.class.getSimpleName(), "sharing error");
                //add your code to handle sharing error
                Toast.makeText(BookDetailActivity.this, "share error", Toast.LENGTH_SHORT).show();

            }
        });

        if (ShareDialog.canShow(ShareLinkContent.class)) {
            ShareLinkContent shareLinkContent = new ShareLinkContent.Builder()
                    .setQuote(mBookInfo.getTitle())
                    .setImageUrl(Uri.parse(ClientUtils.getUrlImage(mBookInfo.getImageId(), ClientUtils.SIZE_DEFAULT)))
                    .setContentUrl(Uri.parse("https://github.com/MrKenitvnn"))
                    .build();

            shareDialog.show(shareLinkContent);
        }
    }
}
