package com.gat.feature.book_detail;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.gat.R;
import com.gat.app.activity.ScreenActivity;
import com.gat.common.util.MZDebug;
import com.gat.data.response.impl.BookInfo;
import com.gat.feature.book_detail.add_to_bookcase.AddToBookcaseActivity;
import com.gat.feature.book_detail.comment.CommentActivity;
import com.gat.feature.book_detail.list_user_sharing_book.ListUserSharingBookActivity;
import com.gat.feature.book_detail.self_update_reading.SelfUpdateReadingActivity;
import com.gat.feature.book_detail.share_book.ShareBookActivity;

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

    @BindView(R.id.text_view_comment_by_user)
    TextView textViewCommentByUser;

    @BindView(R.id.button_comment)
    Button buttonComment;

    @BindView(R.id.text_view_book_description)
    TextView textViewBookDescription;

    @BindView(R.id.text_view_sharing_book)
    TextView textViewSharingBook;


    private CompositeDisposable disposables;

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


        disposables = new CompositeDisposable(
                getPresenter().onGetBookInfoSuccess().subscribe(this::onGetBookInfoSuccess)
        );

        getPresenter().getBookInfo();

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


    private void onGetBookInfoSuccess (BookInfo book) {
        MZDebug.w(" onGetBookInfoSuccess: " + book.toString());

        textViewBookName.setText(book.getTitle());
        textViewBookAuthor.setText(book.getAuthor().get(0).getAuthorName());
        textViewRating.setText(String.valueOf(book.getRateAvg()));
        ratingBarBook.setRating(book.getRateAvg());
        textViewSharingBook.setText(String.valueOf(book.getSharingCount()));
        textViewBookDescription.setText(book.getDescription());
    }




}
