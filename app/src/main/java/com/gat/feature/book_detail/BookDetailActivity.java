package com.gat.feature.book_detail;

import android.os.Bundle;
import android.support.annotation.Nullable;
import com.gat.R;
import com.gat.app.activity.ScreenActivity;

/**
 * Created by mryit on 4/16/2017.
 */

public class BookDetailActivity extends ScreenActivity<BookDetailScreen, BookDetailPresenter>{

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



}
