package com.gat.feature.bookdetailborrow;

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
import com.gat.repository.entity.Data;
import com.gat.repository.entity.book.BookDetailEntity;

import butterknife.BindView;
import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by root on 26/04/2017.
 */

public class BookDetailBorrowActivity extends ScreenActivity<BookDetailBorrowScreen,BookDetailBorrowPresenter>{

    @BindView(R.id.txtTitle)
    TextView txtTitle;

    @BindView(R.id.imgSave)
    ImageView imgSave;

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
    TextView txtNumberComment; @BindView(R.id.layoutSendRequest)
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


    @BindView(R.id.layoutParrentUnReturn)
    RelativeLayout layoutParrentUnReturn;


    @BindView(R.id.rltLostClose)
    RelativeLayout rltLostClose;

    @BindView(R.id.rltRejectClose)
    RelativeLayout rltRejectClose;


    @BindView(R.id.layoutParrentWaitForTurn)
    RelativeLayout layoutParrentWaitForTurn;

    @BindView(R.id.txtWaitForTurnMessage)
    TextView txtWaitForTurnMessage;

    @BindView(R.id.layoutParrentDeleteRequest)
    RelativeLayout layoutParrentDeleteRequest;

    @BindView(R.id.layoutBottomLeft)
    RelativeLayout layoutBottomLeft;

    @BindView(R.id.layoutBottomRight)
    LinearLayout layoutBottomRight;

    @BindView(R.id.rltOverLayBorrow)
    RelativeLayout rltOverLayBorrow;

    @BindView(R.id.rltOverLayReturnBook)
    RelativeLayout rltOverLayReturnBook;

    @BindView(R.id.rltOverLayCancelRequest)
    RelativeLayout rltOverLayCancelRequest;

    @BindView(R.id.rltOverLayUnreturn)
    RelativeLayout rltOverLayUnreturn;



    @BindView(R.id.layoutContactingBorder)
    RelativeLayout layoutContactingBorder;

    @BindView(R.id.layoutBorrowBookBorder)
    RelativeLayout layoutBorrowBookBorder;

    @BindView(R.id.layoutReturnBookBorder)
    RelativeLayout layoutReturnBookBorder;

    @BindView(R.id.rltCheckCancel)
    RelativeLayout rltCheckCancel;

    @BindView(R.id.layoutReturnBook)
    RelativeLayout layoutReturnBook;

    @BindView(R.id.rltCheckReturn)
    RelativeLayout rltCheckReturn;

    @BindView(R.id.rltCheckUnreturn)
    RelativeLayout rltCheckUnreturn;



    private CompositeDisposable disposablesBookDetail;
    private int recordStatus = 0;
    int borrowingRecordId = 0;
    private BookDetailEntity bookDetail;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        disposablesBookDetail = new CompositeDisposable(getPresenter().getResponseBookDetail().subscribe(this::getBookDetailSuccess),
                getPresenter().onErrorBookDetail().subscribe(this::getBookDetailError));
        initView();
    }

    private void initView(){
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

    private void updateView(){
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

        BookDetailEntity.OwnerInfo ownerInfo = bookDetail.getOwnerInfo();
        if(ownerInfo != null) {
            if (!Strings.isNullOrEmpty(ownerInfo.getName())) {
                txtBorrowerName.setText(ownerInfo.getName());
                txtChat.setText("Nhắn tin cho " + ownerInfo.getName());
            }
            if (!Strings.isNullOrEmpty(ownerInfo.getAddress())) {
                txtBorrowerAddress.setText(ownerInfo.getAddress());
            }
            if (!Strings.isNullOrEmpty(ownerInfo.getImageId())) {
                String url = ClientUtils.getUrlImage(ownerInfo.getImageId(), Constance.IMAGE_SIZE_ORIGINAL);
                ClientUtils.setImage(imgBorrower, R.drawable.ic_profile, url);
            }
        }
        ClientUtils.showToast("Record status:"+recordStatus);
        switch (recordStatus){
            case 0:
                //wait to confirm
                layoutSendRequest.setVisibility(View.VISIBLE);
                txtDateSendRequest.setText(ClientUtils.getDateFromString(bookDetail.getRequestTime()));
                layoutParrentAgreed.setVisibility(View.VISIBLE);
                layoutParrentRejected.setVisibility(View.VISIBLE);
                rltRejectClose.setVisibility(View.GONE);
                layoutBottomLeft.setVisibility(View.GONE);
                break;
            case 1:
                //on hold
                layoutSendRequest.setVisibility(View.VISIBLE);
                txtDateSendRequest.setText(ClientUtils.getDateFromString(bookDetail.getRequestTime()));
                txtWaitForTurnMessage.setVisibility(View.VISIBLE);
                layoutParrentWaitForTurn.setVisibility(View.VISIBLE);
                layoutBottomLeft.setVisibility(View.GONE);
                break;
            case 2:
                //contacting
                layoutSendRequest.setVisibility(View.VISIBLE);
                txtDateSendRequest.setText(ClientUtils.getDateFromString(bookDetail.getRequestTime()));
                layoutParrentContacting.setVisibility(View.VISIBLE);
                layoutParrentLost.setVisibility(View.VISIBLE);
                layoutBottomLeft.setVisibility(View.VISIBLE);
                rltCheckReturn.setVisibility(View.GONE);
                rltCheckCancel.setVisibility(View.GONE);
                break;
            case 3:
                //borrowing
                layoutSendRequest.setVisibility(View.VISIBLE);
                layoutStartBorrow.setVisibility(View.VISIBLE);
                txtDateSendRequest.setText(ClientUtils.getDateFromString(bookDetail.getRequestTime()));
                txtDateStartBorrow.setText(ClientUtils.getDateFromString(bookDetail.getBorrowTime()));

                layoutParrentContacting.setVisibility(View.VISIBLE);
                layoutParrentBorrowBook.setVisibility(View.VISIBLE);
                layoutBottomLeft.setVisibility(View.VISIBLE);
                rltOverLayBorrow.setVisibility(View.GONE);
                rltCheckReturn.setVisibility(View.GONE);
                rltCheckCancel.setVisibility(View.GONE);
                layoutContactingBorder.setVisibility(View.VISIBLE);
                layoutBorrowBookBorder.setVisibility(View.VISIBLE);
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
                layoutBottomLeft.setVisibility(View.VISIBLE);
                layoutContactingBorder.setVisibility(View.VISIBLE);
                layoutBorrowBookBorder.setVisibility(View.VISIBLE);
                layoutReturnBookBorder.setVisibility(View.VISIBLE);
                rltCheckCancel.setVisibility(View.GONE);
                rltOverLayBorrow.setVisibility(View.GONE);
                rltOverLayReturnBook.setVisibility(View.GONE);
                rltOverLayCancelRequest.setVisibility(View.GONE);
                layoutReturnBook.setBackground(getResources().getDrawable(R.drawable.bg_layout_filter_book));
                break;
            case 5:
                //reject
                layoutSendRequest.setVisibility(View.VISIBLE);
                txtDateSendRequest.setText(ClientUtils.getDateFromString(bookDetail.getRequestTime()));
                layoutRejectRequest.setVisibility(View.VISIBLE);
                txtDateRejectRequest.setText(ClientUtils.getDateFromString(bookDetail.getRejectTime()));
                layoutParrentRejected.setVisibility(View.VISIBLE);
                break;
            case 6:
                //cancled
                layoutSendRequest.setVisibility(View.VISIBLE);
                txtDateSendRequest.setText(ClientUtils.getDateFromString(bookDetail.getRequestTime()));
                layoutDeletedRequest.setVisibility(View.VISIBLE);
                txtDateDeleteRequest.setText(ClientUtils.getDateFromString(bookDetail.getRejectTime()));
                layoutParrentLost.setVisibility(View.VISIBLE);
                break;
            case 7:
                //unreturnd
                layoutSendRequest.setVisibility(View.VISIBLE);
                txtDateSendRequest.setText(ClientUtils.getDateFromString(bookDetail.getRequestTime()));
                layoutStartBorrow.setVisibility(View.VISIBLE);
                txtDateStartBorrow.setText(ClientUtils.getDateFromString(bookDetail.getBorrowTime()));

                layoutParrentContacting.setVisibility(View.VISIBLE);
                layoutContactingBorder.setVisibility(View.VISIBLE);
                layoutParrentBorrowBook.setVisibility(View.VISIBLE);
                layoutBorrowBookBorder.setVisibility(View.VISIBLE);
                layoutParrentUnReturn.setVisibility(View.VISIBLE);

                layoutBottomLeft.setVisibility(View.VISIBLE);
                rltCheckUnreturn.setVisibility(View.VISIBLE);
                rltOverLayBorrow.setVisibility(View.GONE);
                rltOverLayReturnBook.setVisibility(View.GONE);
                rltOverLayCancelRequest.setVisibility(View.GONE);
                rltOverLayUnreturn.setVisibility(View.GONE);
                rltCheckReturn.setVisibility(View.GONE);
                rltCheckCancel.setVisibility(View.GONE);
                break;
        }
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
