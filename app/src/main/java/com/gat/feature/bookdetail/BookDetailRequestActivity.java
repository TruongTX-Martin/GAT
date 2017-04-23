package com.gat.feature.bookdetail;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.gat.R;
import com.gat.app.activity.ScreenActivity;
import com.gat.common.util.ClientUtils;
import com.gat.common.util.Constance;
import com.gat.common.util.Strings;
import com.gat.data.response.ResponseData;
import com.gat.data.response.ServerResponse;
import com.gat.repository.entity.Data;
import com.gat.repository.entity.book.BookDetailEntity;

import butterknife.BindView;
import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by root on 23/04/2017.
 */

public class BookDetailRequestActivity extends ScreenActivity<BookDetailRequestScreen, BookDetailRequestPresenter> {

    @BindView(R.id.imgBack)
    ImageView imgBack;

    @BindView(R.id.imgSave)
    ImageView imgSave;

    @BindView(R.id.txtTitle)
    TextView txtTitle;

    @BindView(R.id.imgBorrower)
    CircleImageView imgBorrower;

    @BindView(R.id.txtBorrowerName)
    TextView txtBorrowerName;

    @BindView(R.id.txtBorrowerAddress)
    TextView txtBorrowerAddress;

    @BindView(R.id.txtChat)
    TextView txtChat;

    @BindView(R.id.imgEditionBook)
    ImageView imgEditionBook;

    @BindView(R.id.txtEditionName)
    TextView txtEditionName;

    @BindView(R.id.txtEditionAuthor)
    TextView txtEditionAuthor;

    @BindView(R.id.ratingBar)
    RatingBar ratingBar;

    @BindView(R.id.txtNumberComment)
    TextView txtNumberComment;

    @BindView(R.id.layoutSendRequest)
    LinearLayout layoutSendRequest;

    @BindView(R.id.layoutStartBorrow)
    LinearLayout layoutStartBorrow;

    @BindView(R.id.layoutReturnDate)
    LinearLayout layoutReturnDate;

    @BindView(R.id.txtDateSendRequest)
    TextView txtDateSendRequest;

    @BindView(R.id.txtDateStartBorrow)
    TextView txtDateStartBorrow;

    @BindView(R.id.txtDateReturn)
    TextView txtDateReturn;
    int borrowingRecordId = 0;
    private BookDetailEntity bookDetail;
    private int recordStatus = 0;
    private CompositeDisposable disposablesBookDetail;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        disposablesBookDetail = new CompositeDisposable(getPresenter().getResponseBookDetail().subscribe(this::getBookDetailSuccess),
                getPresenter().onErrorBookDetail().subscribe(this::getBookDetailError));
        initView();
    }

    private void initView() {
        txtTitle.setText("CHI TIẾT YÊU CẦU");
        imgSave.setVisibility(View.GONE);
        borrowingRecordId = getIntent().getIntExtra("BorrowingRecordId", 0);
        getPresenter().requestBookDetail(borrowingRecordId);
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

    private void updateView() {
        if (bookDetail != null) {
            BookDetailEntity.BorrowerInfo borrowerInfo = bookDetail.getBorrowerInfo();
            if (borrowerInfo != null) {
                if (!Strings.isNullOrEmpty(borrowerInfo.getName())) {
                    txtBorrowerName.setText(borrowerInfo.getName());
                    txtChat.setText("Nhắn tin cho " + borrowerInfo.getName());
                }
                if (!Strings.isNullOrEmpty(borrowerInfo.getAddress())) {
                    txtBorrowerAddress.setText(borrowerInfo.getAddress());
                }
                if (!Strings.isNullOrEmpty(borrowerInfo.getImageId())) {
                    String url = ClientUtils.getUrlImage(borrowerInfo.getImageId(), Constance.IMAGE_SIZE_ORIGINAL);
                    ClientUtils.setImage(imgBorrower, R.drawable.ic_profile, url);
                }
            }
            BookDetailEntity.EditionInfo editionInfo = bookDetail.getEditionInfo();
            if (editionInfo != null) {
                if(!Strings.isNullOrEmpty(editionInfo.getTitle())) {
                    txtEditionName.setText(editionInfo.getTitle());
                }
                if(!Strings.isNullOrEmpty(editionInfo.getAuthor())) {
                    txtEditionAuthor.setText(editionInfo.getAuthor());
                }
                txtNumberComment.setText(editionInfo.getReviewCount()+"");
                if(!Strings.isNullOrEmpty(editionInfo.getImageId())) {
                    String url = ClientUtils.getUrlImage(editionInfo.getImageId(), Constance.IMAGE_SIZE_ORIGINAL);
                    ClientUtils.setImage(imgEditionBook, R.drawable.ic_profile, url);
                }
                ratingBar.setNumStars(editionInfo.getRateAvg());
            }

            switch (recordStatus){
                case 0:
                    //wait to confirm
                    break;
                case 1:
                    //on hold
                    break;
                case 2:
                    //contacting
                    break;
                case 3:
                    //borrowing
                    layoutSendRequest.setVisibility(View.VISIBLE);
                    layoutStartBorrow.setVisibility(View.VISIBLE);
                    txtDateSendRequest.setText(ClientUtils.getDateFromString(bookDetail.getRequestTime()));
                    txtDateStartBorrow.setText(ClientUtils.getDateFromString(bookDetail.getBorrowTime()));
                    break;
                case 4:
                    //completted
                    break;
                case 5:
                    //reject
                    break;
                case 6:
                    //cancled
                    break;
                case 7:
                    //unreturnd
                    break;
            }
        }
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.layout_bookdetail_activity;
    }

    @Override
    protected Class<BookDetailRequestPresenter> getPresenterClass() {
        return BookDetailRequestPresenter.class;
    }

    @Override
    protected BookDetailRequestScreen getDefaultScreen() {
        return BookDetailRequestScreen.instance();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposablesBookDetail.dispose();
    }
}
