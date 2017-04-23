package com.gat.feature.book_detail.add_to_bookcase;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.res.ResourcesCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import com.gat.R;
import com.gat.app.activity.ScreenActivity;
import com.gat.common.util.MZDebug;
import com.gat.data.response.impl.BookInfo;
import com.gat.data.response.impl.BookInstanceInfo;

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

    private CompositeDisposable disposable;

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

        disposable = new CompositeDisposable(
                getPresenter().onGetBookTotalInstanceSuccess().subscribe(this::onGetBookTotalInstanceSuccess),
                getPresenter().onAddBookInstanceSuccess().subscribe(this::onAddBookInstanceSuccess)
        );

        setupBookInfoDisplay(getScreen().bookInfo());

        getPresenter().setEditionId(getScreen().bookInfo().getEditionId());
        getPresenter().getBookTotalInstance();
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

    @OnClick(R.id.image_view_save)
    void onSave () {
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
        textViewBookAuthor.setText(bookInfo.getAuthor() == null ? "" : bookInfo.getAuthor().get(0).getAuthorName());
        ratingBarBook.setRating(bookInfo.getRateAvg());
        textViewRating.setText(String.valueOf(bookInfo.getRateAvg()));
    }

    private void updateImageState () {
        if (mNewTotalBook <= mTotalBook) {
            imageViewSave.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_check_gray, null));
            imageViewSave.setClickable(false);
            return;
        } else {
            imageViewSave.setClickable(true);
            imageViewSave.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_check_green, null));
        }
    }

    private void onGetBookTotalInstanceSuccess(BookInstanceInfo bookInstanceInfo) {
        mTotalBook = bookInstanceInfo.getBorrowingTotal() +
                bookInstanceInfo.getLostTotal() +
                bookInstanceInfo.getNotSharingToal() +
                bookInstanceInfo.getSharingTotal();
        mNewTotalBook = mTotalBook;
        textViewTotalBook.setText(String.valueOf(mTotalBook));
        if (bookInstanceInfo.getNotSharingToal() > 0) {
            toggleButton.setChecked(false);
        } else {
            toggleButton.setChecked(true);
        }
    }

    private void onAddBookInstanceSuccess (String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        finish();
    }

}
