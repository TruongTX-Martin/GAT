package com.gat.feature.book_detail.add_to_bookcase;

import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.res.ResourcesCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.ToggleButton;
import com.gat.R;
import com.gat.app.activity.ScreenActivity;
import com.gat.common.util.ClientUtils;
import com.gat.common.util.MZDebug;
import com.gat.data.response.impl.BookInfo;
import com.gat.data.response.impl.BookInstanceInfo;
import com.gat.feature.main.MainActivity;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by mryit on 4/17/2017.
 */

public class AddToBookcaseActivity extends ScreenActivity<AddToBookcaseScreen, AddToBookcasePresenter> {
    private final static String TAG = AddToBookcaseActivity.class.getName();

    @BindView(R.id.image_view_save)
    ImageView imageViewSave;

    @BindView(R.id.text_view_total_book)
    TextView textViewTotalBook;

    @BindView(R.id.image_view_book_cover)
    ImageView imageViewBookCover;

    @BindView(R.id.text_view_book_name)
    TextView textViewBookName;

    @BindView(R.id.text_view_book_author)
    TextView textViewBookAuthor;

    @BindView(R.id.rating_bar_book)
    RatingBar ratingBarBook;

    @BindView(R.id.text_view_rating_average)
    TextView textViewRating;

    @BindView(R.id.image_view_minus)
    ImageView imageViewMinus;

    @BindView(R.id.image_view_plus)
    ImageView imageViewPlus;

    @BindView(R.id.toggle_button)
    ToggleButton toggleButton;

    private int mTotalBook = 0;
    private int mNewTotalBook = 0;
    private int mFirstSharing = 1; // 1 : toggle = true, 0: toggle = false
    private boolean isStateChanged = false;

    private CompositeDisposable disposable;

    private AlertDialog progressDialog;
    private AlertDialog changedValueDialog;
    private AlertDialog errorDialog;
    private AlertDialog unAuthorizationDialog;


    @Override
    protected int getLayoutResource() {
        return R.layout.activity_add_to_bookcase;
    }

    @Override
    protected Class<AddToBookcasePresenter> getPresenterClass() {
        return AddToBookcasePresenter.class;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        progressDialog = ClientUtils.createLoadingDialog(AddToBookcaseActivity.this);

        disposable = new CompositeDisposable(
                getPresenter().onGetBookTotalInstanceSuccess().subscribe(this::onGetBookTotalInstanceSuccess),
                getPresenter().onAddBookInstanceSuccess().subscribe(this::onAddBookInstanceSuccess),
                getPresenter().onAddBookInstanceFailure().subscribe(this::onFailure),
                getPresenter().onUnAuthorization().subscribe(this::onUnAuthorization)
        );
    }

    @Override
    protected void onResume() {
        super.onResume();

        showProgress();
        setupBookInfoDisplay(getScreen().bookInfo());
        getPresenter().setBookInfo(getScreen().bookInfo());
        getPresenter().setReadingId(getScreen().readingId());

        getPresenter().getBookTotalInstance();
        toggleButton.setOnClickListener(view -> {
            int newSharing = 0;
            if (toggleButton.isChecked()) {
                newSharing = 1;
            }

            if (mFirstSharing == newSharing) {
                imageViewSave.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_check_gray, null));
                imageViewSave.setClickable(false);
                isStateChanged = false;
            } else {
                imageViewSave.setClickable(true);
                imageViewSave.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_check_green, null));
                isStateChanged = true;
            }
        });
    }

    @Override
    protected void onDestroy() {
        disposable.dispose();

        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        if (unAuthorizationDialog != null) {
            unAuthorizationDialog.dismiss();
        }
        if (changedValueDialog != null) {
            changedValueDialog.dismiss();
        }
        if (errorDialog != null) {
            errorDialog.dismiss();
        }

        super.onDestroy();
    }

    @OnClick(R.id.image_view_back)
    void onBack () {

        if ( ! isStateChanged) {
            finish();
            return;
        }

        // show dialog ask for discard
        ClientUtils.showChangedValueDialog(this);
    }

    @Override
    public void onBackPressed() {

        if (isStateChanged) {
            changedValueDialog = ClientUtils.showChangedValueDialog(this);
        } else {
            finish();
        }
    }

    @OnClick(R.id.image_view_save)
    void onSave () {
        showProgress();
        imageViewSave.setClickable(false);
        getPresenter().addBookInstance(toggleButton.isChecked() ? 1:0, mNewTotalBook - mTotalBook);
    }

    @OnClick(R.id.image_view_minus)
    void onMinus () {
        mNewTotalBook -= 1;
        if (mNewTotalBook == mTotalBook) {
            imageViewMinus.setVisibility(View.GONE);
        }
        textViewTotalBook.setText(String.valueOf(mNewTotalBook));
        updateImageState();
    }

    @OnClick(R.id.image_view_plus)
    void onPlus () {
        mNewTotalBook += 1;
        imageViewMinus.setVisibility(View.VISIBLE);
        textViewTotalBook.setText(String.valueOf(mNewTotalBook));
        updateImageState();
    }

    private void setupBookInfoDisplay (BookInfo bookInfo) {
        MZDebug.w(TAG, bookInfo.toString());
        textViewBookName.setText(bookInfo.getTitle());

        if ( ! TextUtils.isEmpty(getScreen().authorName())) {
            textViewBookAuthor.setText(getScreen().authorName());
        }
        ratingBarBook.setRating(bookInfo.getRateAvg());
        textViewRating.setText(String.valueOf(bookInfo.getRateAvg()));

        if ( ! TextUtils.isEmpty(bookInfo.getImageId())) {
            ClientUtils.setImage(this, imageViewBookCover, R.drawable.default_book_cover,
                    ClientUtils.getUrlImage(bookInfo.getImageId(), ClientUtils.SIZE_SMALL));
        }
    }

    private void updateImageState () {
        if (mNewTotalBook <= mTotalBook) {
            imageViewSave.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_check_gray, null));
            imageViewSave.setClickable(false);
            isStateChanged = false;
        } else {
            imageViewSave.setClickable(true);
            imageViewSave.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_check_green, null));
            isStateChanged = true;
        }
    }

    private void onGetBookTotalInstanceSuccess(BookInstanceInfo bookInstanceInfo) {
        hideProgress();

        mTotalBook = bookInstanceInfo.getBorrowingTotal() +
                bookInstanceInfo.getLostTotal() +
                bookInstanceInfo.getNotSharingTotal() +
                bookInstanceInfo.getSharingTotal();
        mNewTotalBook = mTotalBook;
        textViewTotalBook.setText(String.valueOf(mTotalBook));
        if (bookInstanceInfo.getNotSharingTotal() > 0) {
            toggleButton.setChecked(false);
            mFirstSharing = 0;
        } else {
            toggleButton.setChecked(true);
            mFirstSharing = 1;
        }

    }

    private void onAddBookInstanceSuccess (String message) {
        hideProgress();
        finish();
    }

    private void onFailure (String message) {
        hideProgress();
        imageViewSave.setClickable(true);
        errorDialog = ClientUtils.showDialogError(this, getString(R.string.err), message);
    }

    private void showProgress () {
        progressDialog.show();
    }

    private void hideProgress () {
        if (progressDialog.isShowing()) {
            progressDialog.hide();
        }
    }

    void onUnAuthorization (String message) {
        unAuthorizationDialog = ClientUtils.showDialogUnAuthorization(this, this, message);
    }
}
