package com.gat.feature.bookdetailrequest;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gat.R;
import com.gat.app.activity.ScreenActivity;
import com.gat.common.util.ClientUtils;
import com.gat.common.util.Constance;
import com.gat.common.util.Strings;
import com.gat.data.response.ResponseData;
import com.gat.data.response.ServerResponse;
import com.gat.data.user.PaperUserDataSource;
import com.gat.repository.entity.Data;
import com.gat.repository.entity.book.BookDetailEntity;
import com.google.android.gms.vision.text.Text;

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

    @BindView(R.id.layoutRejectRequest)
    LinearLayout layoutRejectRequest;

    @BindView(R.id.layoutDeletedRequest)
    LinearLayout layoutDeletedRequest;

    @BindView(R.id.txtDateSendRequest)
    TextView txtDateSendRequest;

    @BindView(R.id.txtDateStartBorrow)
    TextView txtDateStartBorrow;

    @BindView(R.id.txtDateReturn)
    TextView txtDateReturn;

    @BindView(R.id.txtDateRejectRequest)
    TextView txtDateRejectRequest;

    @BindView(R.id.txtDateDeleteRequest)
    TextView txtDateDeleteRequest;

    @BindView(R.id.layoutParrentContacting)
    RelativeLayout layoutParrentContacting;

    @BindView(R.id.layoutParrentBorrowBook)
    RelativeLayout layoutParrentBorrowBook;

    @BindView(R.id.layoutParrentReturnBook)
    RelativeLayout layoutParrentReturnBook;

    @BindView(R.id.layoutParrentLost)
    RelativeLayout layoutParrentLost;

    @BindView(R.id.layoutParrentAgreed)
    RelativeLayout layoutParrentAgreed;

    @BindView(R.id.layoutParrentRejected)
    RelativeLayout layoutParrentRejected;

    @BindView(R.id.layoutParrentWaitForTurn)
    RelativeLayout layoutParrentWaitForTurn;

    @BindView(R.id.txtWaitForTurnMessage)
    TextView txtWaitForTurnMessage;

    @BindView(R.id.layoutParrentDeleteRequest)
    RelativeLayout layoutParrentDeleteRequest;


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
        layoutParrentContacting.setVisibility(View.GONE);
        layoutParrentBorrowBook.setVisibility(View.GONE);
        layoutParrentReturnBook.setVisibility(View.GONE);
        layoutParrentLost.setVisibility(View.GONE);
        layoutParrentAgreed.setVisibility(View.GONE);
        layoutParrentRejected.setVisibility(View.GONE);
        layoutParrentDeleteRequest.setVisibility(View.GONE);
        layoutParrentWaitForTurn.setVisibility(View.GONE);
        txtWaitForTurnMessage.setVisibility(View.GONE);

        layoutSendRequest.setVisibility(View.GONE);
        layoutStartBorrow.setVisibility(View.GONE);
        layoutReturnDate.setVisibility(View.GONE);
        layoutRejectRequest.setVisibility(View.GONE);
        layoutDeletedRequest.setVisibility(View.GONE);
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
                ratingBar.setNumStars((int)editionInfo.getRateAvg());
            }
            ClientUtils.showToast("Record status:"+ recordStatus);
            switch (recordStatus){
                case 0:
                    //wait to confirm
                    layoutSendRequest.setVisibility(View.VISIBLE);
                    txtDateSendRequest.setText(ClientUtils.getDateFromString(bookDetail.getRequestTime()));
                    layoutParrentAgreed.setVisibility(View.VISIBLE);
                    layoutParrentRejected.setVisibility(View.VISIBLE);
                    break;
                case 1:
                    //on hold
                    layoutSendRequest.setVisibility(View.VISIBLE);
                    txtDateSendRequest.setText(ClientUtils.getDateFromString(bookDetail.getRequestTime()));
                    txtWaitForTurnMessage.setVisibility(View.VISIBLE);
                    layoutParrentWaitForTurn.setVisibility(View.VISIBLE);
                    break;
                case 2:
                    //contacting
                    layoutSendRequest.setVisibility(View.VISIBLE);
                    txtDateSendRequest.setText(ClientUtils.getDateFromString(bookDetail.getRequestTime()));
                    layoutParrentContacting.setVisibility(View.VISIBLE);
                    layoutParrentBorrowBook.setVisibility(View.VISIBLE);
                    layoutParrentReturnBook.setVisibility(View.VISIBLE);
                    layoutParrentLost.setVisibility(View.VISIBLE);
                    break;
                case 3:
                    //borrowing
                    layoutSendRequest.setVisibility(View.VISIBLE);
                    layoutStartBorrow.setVisibility(View.VISIBLE);
                    txtDateSendRequest.setText(ClientUtils.getDateFromString(bookDetail.getRequestTime()));
                    txtDateStartBorrow.setText(ClientUtils.getDateFromString(bookDetail.getBorrowTime()));
                    layoutParrentContacting.setVisibility(View.VISIBLE);
                    layoutParrentBorrowBook.setVisibility(View.VISIBLE);
                    layoutParrentReturnBook.setVisibility(View.VISIBLE);
                    layoutParrentLost.setVisibility(View.VISIBLE);
                    break;
                case 4:
                    //completted
                    layoutSendRequest.setVisibility(View.VISIBLE);
                    layoutStartBorrow.setVisibility(View.VISIBLE);
                    layoutReturnDate.setVisibility(View.VISIBLE);
                    txtDateSendRequest.setText(ClientUtils.getDateFromString(bookDetail.getRequestTime()));
                    txtDateStartBorrow.setText(ClientUtils.getDateFromString(bookDetail.getBorrowTime()));
                    txtDateReturn.setText(ClientUtils.getDateFromString(bookDetail.getCompleteTime()));
                    layoutParrentContacting.setVisibility(View.VISIBLE);
                    layoutParrentBorrowBook.setVisibility(View.VISIBLE);
                    layoutParrentReturnBook.setVisibility(View.VISIBLE);
                    break;
                case 5:
                    //reject
                    layoutRejectRequest.setVisibility(View.VISIBLE);
                    txtDateRejectRequest.setText(ClientUtils.getDateFromString(bookDetail.getRejectTime()));
                    layoutRejectRequest.setVisibility(View.VISIBLE);
                    break;
                case 6:
                    //cancled
                    layoutSendRequest.setVisibility(View.VISIBLE);
                    txtDateSendRequest.setText(ClientUtils.getDateFromString(bookDetail.getRequestTime()));
                    layoutDeletedRequest.setVisibility(View.VISIBLE);
                    txtDateDeleteRequest.setText(ClientUtils.getDateFromString(bookDetail.getRejectTime()));
                    layoutDeletedRequest.setVisibility(View.VISIBLE);
                    break;
                case 7:
                    //unreturnd
                    layoutSendRequest.setVisibility(View.VISIBLE);
                    txtDateSendRequest.setText(ClientUtils.getDateFromString(bookDetail.getRequestTime()));
                    layoutStartBorrow.setVisibility(View.VISIBLE);
                    txtDateStartBorrow.setText(ClientUtils.getDateFromString(bookDetail.getBorrowTime()));
                    layoutParrentContacting.setVisibility(View.VISIBLE);
                    layoutParrentBorrowBook.setVisibility(View.VISIBLE);
                    layoutParrentLost.setVisibility(View.VISIBLE);
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
