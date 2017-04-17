package com.gat.feature.book_detail;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ScrollView;

import com.gat.R;
import com.gat.app.activity.ScreenActivity;
import com.gat.feature.book_detail.add_to_bookcase.AddToBookcaseActivity;
import com.gat.feature.book_detail.comment.CommentActivity;
import com.gat.feature.book_detail.list_user_sharing_book.ListUserSharingBookActivity;
import com.gat.feature.book_detail.self_update_reading.SelfUpdateReadingActivity;
import com.gat.feature.book_detail.share_book.ShareBookActivity;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by mryit on 4/16/2017.
 */

public class BookDetailActivity extends ScreenActivity<BookDetailScreen, BookDetailPresenter>{

    @BindView(R.id.scroll_view)
    ScrollView scrollView;





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
        return BookDetailScreen.instance();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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


}
