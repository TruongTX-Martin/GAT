package com.gat.feature.book_detail;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.internal.CallbackManagerImpl;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.ShareOpenGraphAction;
import com.facebook.share.model.ShareOpenGraphContent;
import com.facebook.share.model.ShareOpenGraphObject;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.widget.ShareDialog;
import com.gat.R;
import com.gat.app.activity.ScreenActivity;
import com.gat.common.customview.ViewMoreTextView;
import com.gat.common.util.ClientUtils;
import com.gat.common.util.MZDebug;
import com.gat.data.response.UserResponse;
import com.gat.data.response.impl.BookInfo;
import com.gat.data.response.impl.BookReadingInfo;
import com.gat.data.response.impl.EvaluationItemResponse;
import com.gat.feature.book_detail.adapter.EvaluationItemAdapter;
import com.gat.feature.book_detail.add_to_bookcase.AddToBookcaseActivity;
import com.gat.feature.book_detail.add_to_bookcase.AddToBookcaseScreen;
import com.gat.feature.book_detail.comment.CommentActivity;
import com.gat.feature.book_detail.comment.CommentScreen;
import com.gat.feature.book_detail.list_user_sharing_book.ListUserSharingBookActivity;
import com.gat.feature.book_detail.list_user_sharing_book.ListUserSharingBookScreen;
import com.gat.feature.book_detail.self_update_reading.ReadingState;
import com.gat.feature.book_detail.self_update_reading.SelfUpdateReadingActivity;
import com.gat.feature.book_detail.self_update_reading.SelfUpdateReadingScreen;
import com.gat.repository.entity.User;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by mryit on 4/16/2017.
 */

public class BookDetailActivity extends ScreenActivity<BookDetailScreen, BookDetailPresenter>
        implements RatingBar.OnRatingBarChangeListener, Observer{

    private final String TAG = BookDetailActivity.class.getSimpleName();

    private static final int RC_UPDATE_READING_STATUS = 0x01;
    private static final int RC_UPDATE_COMMENT = 0x02;
    private static final int RC_UPDATE_BOOKCASE = 0x03;
    private static final int RC_VIEW_USER_SHARING = 0x04;
    public static final String KEY_UPDATE_READING_STATUS = "status";
    public static final String KEY_UPDATE_COMMENT = "comment";
    public static final String KEY_UPDATE_BOOKCASE = "bookcase";
    public static final String KEY_VIEW_USER_SHARING = "view_user_sharing";

    @BindView(R.id.image_view_book_cover)
    ImageView imageViewBookCover;

    @BindView(R.id.scroll_view)
    NestedScrollView scrollView;

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

    @BindView(R.id.ll_comment)
    LinearLayout llComment;

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

    @BindView(R.id.image_view_quote_left)
    ImageView imageViewQuoteLeft;

    @BindView(R.id.image_view_quote_right)
    ImageView imageViewQuoteRight;


    private CompositeDisposable disposables;
    private EvaluationItemAdapter adapter;

    AlertDialog errorDialog;
    AlertDialog loginDialog;
    AlertDialog loadingDialog;
    AlertDialog unAuthorizationDialog;

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

        loadingDialog = ClientUtils.createLoadingDialog(BookDetailActivity.this);
        setupRecyclerViewComments();
        getPresenter().setEditionId(getScreen().editionId());

        gatApplication.getObserverNetworkChange().addObserver(this);

        disposables = new CompositeDisposable(
                getPresenter().onGetBookInfoSuccess().subscribe(this::onGetBookInfoSuccess),
                getPresenter().onGetEditionSharingUsersSuccess().subscribe(this::onGetEditionSharingUsersSuccess),
                getPresenter().onGetBookEvaluationByUser().subscribe(this::onGetBookEvaluationByUser),
                getPresenter().onGetSelfReadingStatusSuccess().subscribe(this::onGetSelfReadingStatusSuccess),
                getPresenter().onGetSelfReadingStatusFailure().subscribe(this::onGetSelfReadingStatusFailure),
                getPresenter().onGetBookEditionEvaluationSuccess().subscribe(this::onGetBookEditionEvaluationSuccess),
                getPresenter().onError().subscribe(this::onError),
                getPresenter().onUserLoggedIn().subscribe(this::onUserLoggedIn),
                getPresenter().onUserNotLoggedIn().subscribe(this::onUserNotLoggedIn),
                getPresenter().onUpdateReadingStatusSuccess().subscribe(this::onUpdateReadingStatusSuccess),
                getPresenter().onUpdateReadingStatusFailure().subscribe(this::onUpdateReadingStatusFailure),
                getPresenter().onGetEvaluationByUserFailure().subscribe(this::onGetEvaluationByUserFailure),
                getPresenter().onRatingSuccess().subscribe(this::onPostRatingSuccess)
        );

        ratingBarUserRate.setOnRatingBarChangeListener(this);

        // setup style RatingBar Comment
        LayerDrawable stars = (LayerDrawable) ratingBarUserRate.getProgressDrawable();
        stars.getDrawable(2).setColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_ATOP);
        stars.getDrawable(0).setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_ATOP);
        stars.getDrawable(1).setColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_ATOP);

        showProgress();
        getPresenter().isUserLoggedIn();
        getPresenter().getEditionSharingUsers();
        getPresenter().getBookEditionEvaluation();
        getPresenter().getBookInfo();
    }

    @Override
    protected void onStart() {
        super.onStart();
        scrollView.smoothScrollBy(0,0);
    }

    @Override
    protected void onDestroy() {
        disposables.dispose();

        if (errorDialog != null) {
            errorDialog.dismiss();
        }
        if (loginDialog != null) {
            loginDialog.dismiss();
        }
        if (loadingDialog != null) {
            loadingDialog.dismiss();
        }
        if (unAuthorizationDialog != null) {
            unAuthorizationDialog.dismiss();
        }

        super.onDestroy();
    }

    @Override
    public void onBackPressed() {

        hideProgress();

        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_UPDATE_READING_STATUS && resultCode == RESULT_OK) {
            MZDebug.w("________________________ BookDetailActivity : onActivityResult : RESULT OK");
            int statusResult = data.getIntExtra(KEY_UPDATE_READING_STATUS, ReadingState.REMOVE);
            updateButtonReadingStatus(statusResult);

        } else if (requestCode == RC_UPDATE_COMMENT && resultCode == RESULT_OK) {
            String comment = data.getStringExtra(KEY_UPDATE_COMMENT);
            textViewCommentByUser.setVisibility(View.VISIBLE);
            textViewCommentByUser.setText(comment == null ? "" : comment);
            imageViewQuoteLeft.setVisibility(View.VISIBLE);
            imageViewQuoteRight.setVisibility(View.VISIBLE);
            buttonComment.setText(getResources().getString(R.string.edit_comment));

            // update in list comments
            if (mEvaluationByUser == null) {
                mEvaluationByUser = new EvaluationItemResponse();
                mEvaluationByUser.setUserId(mUser.userId())
                        .setImageId(mUser.imageId())
                        .setReview(textViewCommentByUser.getText().toString())
                        .setValue(ratingBarUserRate.getRating())
                        .setName(mUser.name())
                        .setSpoiler(false)
                        .setEvaluationTime( (new Date()).getTime());
            } else {
                mEvaluationByUser.setReview(comment);
            }
            adapter.updateItem(mEvaluationByUser);

        } else if (requestCode == CallbackManagerImpl.RequestCodeOffset.Share.toRequestCode()) {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        } else if (requestCode == RC_UPDATE_BOOKCASE && resultCode == RESULT_OK) {
            showProgress();
            getPresenter().getEditionSharingUsers();
        } else if (requestCode == RC_VIEW_USER_SHARING && resultCode == RESULT_OK) {
            showProgress();
            getPresenter().getEditionSharingUsers();
        }
    }

    @OnClick(R.id.image_button_back)
    void onBack () {
        finish();
    }

    @OnClick(R.id.image_button_go_share)
    void onGoShare () {
//        if (mUser == null) {
//            MZDebug.w(TAG, "user = null, show login dialog");
//            showLoginDialog();
//            return;
//        }

        if (mBookInfo == null) {
            return;
        }
        authenFacebookAndShare();
        //showFacebookShareOpenGraph();
    }

    @OnClick(R.id.button_reading_state)
    void updateReadingState () {
        if (mUser == null) {
            showLoginDialog();
            return;
        }

        if (mBookInfo == null) {
            return;
        }

        // nếu sẽ hiển thị là Muốn đọc
        // nhấn vào thì sẽ hiện dấu tích bên trái và gọi api update status;

        // nếu readingStatus >=0 thì hiển thị tên tương ứng
        // + 0: Readed - đã đọc
        // + 1: Reading - đang đọc
        // + 2: To read - sẽ đọc
        // khi nhấp vào thì sẽ gọi SelfUpdateReadingActivity
        if ( (int)buttonReadingState.getTag() == ReadingState.REMOVE) {
            showProgress();
            getPresenter().updateReadingStatus();
            buttonReadingState.setClickable(false);
        } else {
            startForResult(BookDetailActivity.this,
                    SelfUpdateReadingActivity.class,
                    SelfUpdateReadingScreen.instance(mBookInfo.getEditionId(), (int) buttonReadingState.getTag()),
                    RC_UPDATE_READING_STATUS);
        }
    }

    @OnClick(R.id.button_comment)
    void onGoComment () {
        if (mBookInfo == null) { // có thể bắt load lại book info
            return;
        }
        float value = 0;
        String comment = "";
        if (mEvaluationByUser != null) {
            value = mEvaluationByUser.getValue();
            if (ratingBarUserRate.getRating() != 0) {
                value = ratingBarUserRate.getRating();
            }

            comment = TextUtils.isEmpty(mEvaluationByUser.getReview()) ? "" : mEvaluationByUser.getReview();
            if ( ! TextUtils.isEmpty(textViewCommentByUser.getText().toString()) ) {
                comment = textViewCommentByUser.getText().toString();
            }

        }

        startForResult(this, CommentActivity.class, CommentScreen.instance(mBookInfo.getEditionId(), value, comment), RC_UPDATE_COMMENT);
    }

    @OnClick(R.id.image_button_plus)
    void onAddToBookCase () {
        if (null == mBookInfo) {
            return;
        }
        if (mUser == null) {
            showLoginDialog();
            return;
        }
        startForResult(this, AddToBookcaseActivity.class,
                AddToBookcaseScreen.instance(mBookInfo, textViewBookAuthor.getText().toString()), RC_UPDATE_BOOKCASE);
    }

    @OnClick(R.id.button_view_list)
    void onViewListSharingThisBook () {
        if (mListUserSharing == null || mListUserSharing.isEmpty()) {
            return;
        }

        if (mUser == null) {
            showLoginDialog();
            return;
        }

        MZDebug.e("BookDetailActivity: position 0= " + mListUserSharing.get(0).toString() );
        startForResult(this, ListUserSharingBookActivity.class, ListUserSharingBookScreen.instance(mListUserSharing), RC_VIEW_USER_SHARING);
    }

    private void updateButtonReadingStatus (int status) {
        MZDebug.w("__________ BookDetailActivity : updateButtonReadingStatus : status = " + status);
        buttonReadingState.setTag(status);
        switch (status) {
            case ReadingState.REMOVE:
                buttonReadingState.setText(getResources().getString(R.string.want_to_read));
                buttonReadingState.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.arrow_down_white, 0);
                break;

            case ReadingState.RED:
                buttonReadingState.setText(getResources().getString(R.string.red_book));
                buttonReadingState.setCompoundDrawablesWithIntrinsicBounds( R.drawable.ic_check_yellow, 0, R.drawable.arrow_down_white, 0);
                break;

            case ReadingState.READING:
                buttonReadingState.setText(getResources().getString(R.string.reading_book));
                buttonReadingState.setCompoundDrawablesWithIntrinsicBounds( R.drawable.ic_check_yellow, 0, R.drawable.arrow_down_white, 0);
                break;

            case ReadingState.TO_READ:
                buttonReadingState.setText(getResources().getString(R.string.want_to_read));
                buttonReadingState.setCompoundDrawablesWithIntrinsicBounds( R.drawable.ic_check_yellow, 0, R.drawable.arrow_down_white, 0);
                break;
        }
    }

    private void setupRecyclerViewComments () {
        adapter = new EvaluationItemAdapter();
        this.recyclerView.setHasFixedSize(false);
        this.recyclerView.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        this.recyclerView.setNestedScrollingEnabled(false);
        this.recyclerView.setAdapter(adapter);
    }

    private BookInfo mBookInfo;
    private void onGetBookInfoSuccess (BookInfo book) {
        hideProgress();

        mBookInfo = book;
        MZDebug.w(" onGetBookInfoSuccess: " + book.toString());
        textViewBookName.setText(book.getTitle());
        if (book.getAuthor() != null && !book.getAuthor().isEmpty()) {
            textViewBookAuthor.setText(book.getAuthor().get(0).getAuthorName());
        }
        textViewRating.setText(String.valueOf(book.getRateAvg()));
        ratingBarBook.setRating(book.getRateAvg());
        textViewBookDescription.setText(book.getDescription());
        ViewMoreTextView.makeTextViewResizable(textViewBookDescription, 5, "Xem thêm", true);
        if (mBookInfo.getImageId() != null && !mBookInfo.getImageId().isEmpty()) {
            imageViewBookCover.setDrawingCacheEnabled(true);
            ClientUtils.setImage(this, imageViewBookCover, R.drawable.default_book_cover, ClientUtils.getUrlImage(mBookInfo.getImageId(), ClientUtils.SIZE_SMALL));
        }
    }

    private List<EvaluationItemResponse> listEvaluations;
    private void onGetBookEditionEvaluationSuccess (List<EvaluationItemResponse> list) {
        if (list == null || list.isEmpty()) {
            return;
        }

        listEvaluations = list;
        adapter.setItems(list);
    }

    private void onGetSelfReadingStatusSuccess (BookReadingInfo bookReadingInfo) {
        MZDebug.w("onGetSelfReadingStatusSuccess: " + bookReadingInfo.toString());

        updateButtonReadingStatus(bookReadingInfo.getReadingStatus());
    }

    private void onGetSelfReadingStatusFailure (String message) {
        MZDebug.w("onGetSelfReadingStatusFailure _____________________________________ " + message);
        updateButtonReadingStatus(ReadingState.REMOVE);
    }

    private List<UserResponse> mListUserSharing;
    private void onGetEditionSharingUsersSuccess (List<UserResponse> list) {
        MZDebug.w("onGetEditionSharingUsersSuccess: " + list.size());
        hideProgress();

        mListUserSharing = list;
        textViewSharingBook.setText(String.valueOf(list.size()));
    }

    private int avgRating = 0;
    private EvaluationItemResponse mEvaluationByUser;
    private void onGetBookEvaluationByUser (EvaluationItemResponse evaluation) {
        mEvaluationByUser = evaluation;

        MZDebug.w("onGetEditionSharingUsersSuccess: " + evaluation.toString());
        llComment.setVisibility(View.VISIBLE);

        if (evaluation == null || evaluation.getValue() == 0) {
            ratingBarUserRate.setRating(0);
            textViewCommentByUser.setVisibility(View.GONE);
            buttonComment.setVisibility(View.GONE);
        } else {
            avgRating = (int) evaluation.getValue();
            ratingBarUserRate.setRating(evaluation.getValue());

            // nếu chưa rate
            if ( evaluation.getValue() == 0 ) {

                // nếu chưa review
                if (TextUtils.isEmpty(evaluation.getReview())) {
                    textViewCommentByUser.setVisibility(View.GONE);
                    buttonComment.setVisibility(View.GONE);
                } else { // nếu đã review
                    textViewCommentByUser.setVisibility(View.VISIBLE);
                    textViewCommentByUser.setText(evaluation.getReview());
                    buttonComment.setText(getResources().getString(R.string.edit_comment));
                }

            } else { // nếu đã rate thì mới viết sửa được

                buttonComment.setVisibility(View.VISIBLE);
                if (TextUtils.isEmpty(evaluation.getReview())) {
                    textViewCommentByUser.setVisibility(View.GONE);
                    imageViewQuoteLeft.setVisibility(View.GONE);
                    imageViewQuoteRight.setVisibility(View.GONE);

                    buttonComment.setText(getResources().getString(R.string.write_comment));
                } else {
                    textViewCommentByUser.setVisibility(View.VISIBLE);
                    textViewCommentByUser.setText(evaluation.getReview());
                    imageViewQuoteLeft.setVisibility(View.VISIBLE);
                    imageViewQuoteRight.setVisibility(View.VISIBLE);
                    buttonComment.setText(getResources().getString(R.string.edit_comment));
                }
            }
        }
    }

    private void onGetEvaluationByUserFailure (String message) {
        ratingBarUserRate.setRating(0);
        textViewCommentByUser.setVisibility(View.GONE);
        buttonComment.setVisibility(View.GONE);
    }

    private void onError (String message) {
        hideProgress();
        ClientUtils.showDialogError(this, getString(R.string.err), message);
    }

    private User mUser;
    private void onUserLoggedIn (User user) {
        mUser = user;
        MZDebug.w("FINAL onUserLoggedIn: \n\r" + user.toString());
        getPresenter().getBookEvaluationByUser();
        getPresenter().getSelfReadingStatus();
    }

    private void onUserNotLoggedIn (String message) {
        MZDebug.w("FINAL onUserNotLoggedIn");
        // hide button comment, rating, string comment
        llComment.setVisibility(View.GONE);
    }

    private void onUpdateReadingStatusSuccess (String message) {
        hideProgress();
        buttonReadingState.setClickable(true);
        buttonReadingState.setTag(ReadingState.TO_READ);
        buttonReadingState.setCompoundDrawablesWithIntrinsicBounds( R.drawable.ic_check_yellow, 0, R.drawable.arrow_down_white, 0);
    }

    private void onUpdateReadingStatusFailure (String message) {
        hideProgress();
        buttonReadingState.setClickable(true);
        ClientUtils.showDialogError(this, getString(R.string.err), message);
    }


    private LoginManager loginManager;
    private static CallbackManager callbackManager;

    private void authenFacebookAndShare () {
        List<String> permissionNeeds = Arrays.asList("publish_actions");

        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

//        loginManager = LoginManager.getInstance();
//        loginManager.logInWithPublishPermissions(this, permissionNeeds);
//
//        loginManager.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
//            @Override
//            public void onSuccess(LoginResult loginResult) {
//                showFacebookShareOpenGraph();
//            }
//
//            @Override
//            public void onCancel() {
//                MZDebug.w("facebook on cancel");
//            }
//
//            @Override
//            public void onError(FacebookException exception) {
//                MZDebug.w("facebook on error \n\r: " + Log.getStackTraceString(exception));
//            }
//        });



        showFacebookShareOpenGraph();
    }


    private void showFacebookShareOpenGraph () {
        MZDebug.w(TAG, "showFacebookShareOpenGraph");
        SharePhoto photo;
        ShareOpenGraphObject object;
        if (mBookInfo.getImageId().isEmpty()) {
            // Create an object
            object = new ShareOpenGraphObject.Builder()
                    .putString("og:type", "books.book")
                    .putString("og:title", mBookInfo.getTitle())
                    .putString("og:description", mBookInfo.getDescription())
                    .putString("books:isbn", (mBookInfo.getIsbn10() == null || mBookInfo.getIsbn10().isEmpty()) ? "0-553-57340-3" : mBookInfo.getIsbn10())

                    .build();
        } else {

            photo = new SharePhoto.Builder()
                    .setImageUrl(Uri.parse(ClientUtils.getUrlImage(mBookInfo.getImageId(), ClientUtils.SIZE_DEFAULT)))
                    .setUserGenerated(true)
                    .build();

            // if book isbn_13 =null -> get isbn_10, if isbn_10 = null -> default
            String bookIsbn = "1556154844";
            if ( ! TextUtils.isEmpty(mBookInfo.getIsbn13())) {
                bookIsbn = mBookInfo.getIsbn13();
            } else if ( ! TextUtils.isEmpty(mBookInfo.getIsbn10())){
                    bookIsbn = mBookInfo.getIsbn10();
            }

            // Create an object
            object = new ShareOpenGraphObject.Builder()
                    .putString("og:type", "books.book")
                    .putString("og:title", mBookInfo.getTitle())
                    .putString("og:description", mBookInfo.getDescription())
                    .putString("books:isbn", bookIsbn)
                    .putPhoto("og:image", photo)
                    .build();
        }

        // action wants_to_read or reads
        String actionRead = "books.wants_to_read";
        if (buttonReadingState.getTag() != null) {
            if ( (int) buttonReadingState.getTag() == ReadingState.RED) {
                actionRead = "books.reads";
            }
        }

        ShareOpenGraphAction action = new ShareOpenGraphAction.Builder()
                    .setActionType(actionRead)
                    .putObject("book", object)
                    .build();

        // Create the content
        ShareOpenGraphContent content = new ShareOpenGraphContent.Builder()
                .setPreviewPropertyName("book")
                .setAction(action)
                .build();

        ShareDialog shareDialog = new ShareDialog(BookDetailActivity.this);
        shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
            @Override
            public void onSuccess(Sharer.Result result) {
                MZDebug.d(BookDetailActivity.class.getSimpleName(), "shared successfully");
                //add your code to handle successful sharing
            }

            @Override
            public void onCancel() {
                MZDebug.d(BookDetailActivity.class.getSimpleName(), "sharing cancelled");
            }

            @Override
            public void onError(FacebookException error) {
                MZDebug.d(BookDetailActivity.class.getSimpleName(), "SHARING ERROR: \n\r" + error.getMessage());
            }
        });

        if (shareDialog.canShow(ShareLinkContent.class) ) {
            shareDialog.show(BookDetailActivity.this, content);
            MZDebug.w(TAG, "shareDialog is showing");
        } else {
            MZDebug.w(TAG, "shareDialog.canShow(ShareLinkContent.class) = false");
        }
    }

    @Override
    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
        if (fromUser) {
            if (mBookInfo == null) {
                return;
            }
            ratingBarUserRate.setRating(rating);
            showProgress();
            // spoiler default = false
            getPresenter().postRating(mBookInfo.getEditionId(), rating, textViewCommentByUser.getText().toString(), false);
        }
    }

    private void onPostRatingSuccess (String message) {
        MZDebug.w(TAG, "onPostRatingSuccess");
        hideProgress();

        // update in list comments
        if (mEvaluationByUser == null) {
            mEvaluationByUser = new EvaluationItemResponse();
            mEvaluationByUser.setUserId(mUser.userId())
                    .setImageId(mUser.imageId())
                    .setReview(textViewCommentByUser.getText().toString())
                    .setValue(ratingBarUserRate.getRating())
                    .setName(mUser.name())
                    .setSpoiler(false)
                    .setEvaluationTime( (new Date()).getTime());
        } else {
            mEvaluationByUser.setValue(ratingBarUserRate.getRating());
        }
        adapter.updateItem(mEvaluationByUser);
        // update rate avg
        float rateAvg = adapter.rateAvg();
        ratingBarBook.setRating(rateAvg);
        textViewRating.setText(String.valueOf(rateAvg));

        // update button comment
        buttonComment.setVisibility(View.VISIBLE);
        buttonComment.setText(getResources().getString(R.string.write_comment));
        if (mEvaluationByUser != null && ! TextUtils.isEmpty(mEvaluationByUser.getReview())) {
            textViewCommentByUser.setVisibility(View.VISIBLE);
            buttonComment.setText(getResources().getString(R.string.edit_comment));
        }

    }

    private void showProgress () {
        loadingDialog.show();
    }

    private void hideProgress () {
        if (loadingDialog.isShowing()) {
            loadingDialog.hide();
        }
    }

    private void showLoginDialog () {
        loginDialog = ClientUtils.showRequiredLoginDialog(BookDetailActivity.this, this);
    }

    @Override
    public void update(Observable o, Object arg) {

    }
}
