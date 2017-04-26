package com.gat.feature.bookdetailborrow;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.gat.R;
import com.gat.app.activity.ScreenActivity;
import com.gat.common.util.ClientUtils;
import com.gat.data.response.ResponseData;
import com.gat.data.response.ServerResponse;
import com.gat.repository.entity.Data;
import com.gat.repository.entity.book.BookDetailEntity;

import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by root on 26/04/2017.
 */

public class BookDetailBorrowActivity extends ScreenActivity<BookDetailBorrowScreen,BookDetailBorrowPresenter>{


    private CompositeDisposable disposablesBookDetail;
    private BookDetailEntity bookDetail;
    private int recordStatus = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        disposablesBookDetail = new CompositeDisposable(getPresenter().getResponseBookDetail().subscribe(this::getBookDetailSuccess),
                getPresenter().onErrorBookDetail().subscribe(this::getBookDetailError));
    }


    private void getBookDetailError(ServerResponse<ResponseData> error) {
        ClientUtils.showToast("Error:" + error.message());
    }

    private void getBookDetailSuccess(Data data) {
        if (data != null) {
            bookDetail = (BookDetailEntity) data.getDataReturn(BookDetailEntity.class);
            recordStatus = bookDetail.getRecordStatus();
            updateView();
        }
    }

    private void updateView(){
        
    }
    @Override
    protected int getLayoutResource() {
        return R.layout.layout_bookdetail_borrow_activity;
    }

    @Override
    protected Class<BookDetailBorrowPresenter> getPresenterClass() {
        return BookDetailBorrowPresenter.class;
    }
    @Override
    protected BookDetailBorrowScreen getDefaultScreen() {
        return BookDetailBorrowScreen.instance();
    }
}
