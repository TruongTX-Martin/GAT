package com.gat.feature.bookdetailowner;

import android.graphics.Color;
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
import com.gat.feature.bookdetailsender.BookDetailSenderActivity;
import com.gat.feature.bookdetailsender.entity.ChangeStatusResponse;
import com.gat.feature.login.LoginScreen;
import com.gat.feature.main.MainActivity;
import com.gat.feature.message.MessageActivity;
import com.gat.feature.message.presenter.MessageScreen;
import com.gat.feature.personal.entity.RequestStatusInput;
import com.gat.feature.personaluser.PersonalUserActivity;
import com.gat.feature.personaluser.PersonalUserScreen;
import com.gat.feature.start.StartActivity;
import com.gat.repository.entity.Data;
import com.gat.repository.entity.book.BookDetailEntity;

import butterknife.BindView;
import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by root on 26/04/2017.
 */

public class BookDetailOwnerActivity extends ScreenActivity<BookDetailOwnerScreen,BookDetailOwnerPresenter>{

    @BindView(R.id.txtTitle)
    TextView txtTitle;

    @BindView(R.id.imgSave)
    ImageView imgSave;

    @BindView(R.id.imgBack)
    ImageView imgBack;

    @BindView(R.id.txtNumberSharing)
    TextView txtNumberSharing;

    @BindView(R.id.txtNumberReading)
    TextView txtNumberReading;

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

    @BindView(R.id.layoutBorrowBook)
    RelativeLayout layoutBorrowBook;

    @BindView(R.id.layoutParrentReturnBook)
    RelativeLayout layoutParrentReturnBook;

    @BindView(R.id.layoutParrentCancleRequest)
    RelativeLayout layoutParrentCancleRequest;


    @BindView(R.id.layoutParrentLostBook)
    RelativeLayout layoutParrentLostBook;


    @BindView(R.id.layoutParrentAgreed)
    RelativeLayout layoutParrentAgreed;

    @BindView(R.id.layoutParrentRejectedRequest)
    RelativeLayout layoutParrentRejectedRequest;


    @BindView(R.id.layoutParrentUnReturn)
    RelativeLayout layoutParrentUnReturn;


    @BindView(R.id.rltDeleteRequestClose)
    RelativeLayout rltDeleteRequestClose;

    @BindView(R.id.rltRejectClose)
    RelativeLayout rltRejectClose;


    @BindView(R.id.layoutParrentWaitForTurn)
    RelativeLayout layoutParrentWaitForTurn;

    @BindView(R.id.txtWaitForTurnMessage)
    TextView txtWaitForTurnMessage;

    @BindView(R.id.layoutParrentReject)
    RelativeLayout layoutParrentReject;

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

    @BindView(R.id.layoutChat)
    LinearLayout layoutChat;

    @BindView(R.id.imgChat)
    ImageView messageChat;


    private CompositeDisposable disposablesBookDetail;
    private CompositeDisposable disposablesRequestBookByOwner;
    private int recordStatus = 0;
    int borrowingRecordId = 0;
    private BookDetailEntity bookDetail;
    private RequestStatusInput statusInput = new RequestStatusInput();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        disposablesBookDetail = new CompositeDisposable(getPresenter().getResponseBookDetail().subscribe(this::getBookDetailSuccess),
                getPresenter().onErrorBookDetail().subscribe(this::getBookDetailError));

        disposablesRequestBookByOwner = new CompositeDisposable(getPresenter().getResponseChangeStatus().subscribe(this::requestBookByOwnerSuccess),
                getPresenter().onErrorChangeStatus().subscribe(this::getBookDetailError));
        initView();
        handleEvent();
    }

    private void initView(){
        txtTitle.setText("CHI TIẾT YÊU CẦU");
        imgSave.setVisibility(View.GONE);
        txtTitle.setTextColor(Color.parseColor("#000000"));
        imgBack.setImageResource(R.drawable.narrow_back_black);
        // TODO remove intent
        if (getScreen().requestId() != 0) {
            borrowingRecordId = getScreen().requestId();
        } else {
            borrowingRecordId = getIntent().getIntExtra("BorrowingRecordId", 0);
        }

        layoutParrentContacting.setVisibility(View.GONE);
        layoutParrentBorrowBook.setVisibility(View.GONE);
        layoutParrentReturnBook.setVisibility(View.GONE);
        layoutParrentCancleRequest.setVisibility(View.GONE);
        layoutParrentAgreed.setVisibility(View.GONE);
        layoutParrentRejectedRequest.setVisibility(View.GONE);
        layoutParrentReject.setVisibility(View.GONE);
        layoutParrentWaitForTurn.setVisibility(View.GONE);
        layoutParrentLostBook.setVisibility(View.GONE);
        txtWaitForTurnMessage.setVisibility(View.GONE);

        layoutSendRequest.setVisibility(View.GONE);
        layoutStartBorrow.setVisibility(View.GONE);
        layoutReturnDate.setVisibility(View.GONE);
        layoutRejectRequest.setVisibility(View.GONE);
        layoutDeletedRequest.setVisibility(View.GONE);
        requestDetailBook();
    }

    private void handleEvent(){
        imgBack.setOnClickListener(v -> finish());
        layoutParrentAgreed.setOnClickListener(v -> {
            if(recordStatus == 0){
                statusInput.setCurrentStatus(0);
                statusInput.setNewStatus(2);
                statusInput.setRecordId(bookDetail.getRecordId());
                requestBookOwner(statusInput);
            }
        });
        layoutParrentReject.setOnClickListener(v -> {
            if(recordStatus == 0){
                statusInput.setCurrentStatus(0);
                statusInput.setNewStatus(5);
                statusInput.setRecordId(bookDetail.getRecordId());
                requestBookOwner(statusInput);
            }
        });
        layoutParrentBorrowBook.setOnClickListener(v -> {
            if(recordStatus == 2){
                statusInput.setCurrentStatus(2);
                statusInput.setNewStatus(3);
                statusInput.setRecordId(bookDetail.getRecordId());
                requestBookOwner(statusInput);
            }
        });
        layoutParrentCancleRequest.setOnClickListener(v -> {
            if(recordStatus == 2){
                statusInput.setCurrentStatus(2);
                statusInput.setNewStatus(6);
                statusInput.setRecordId(bookDetail.getRecordId());
                requestBookOwner(statusInput);
            }
        });
        imgBorrower.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BookDetailSenderActivity.start(getApplicationContext(), PersonalUserActivity.class, PersonalUserScreen.instance(bookDetail.getBorrowerInfo().getUserId()));
            }
        });
        imgEditionBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        layoutChat.setOnClickListener(v -> {
            if (bookDetail.getBorrowerInfo() != null)
                start(getApplicationContext(), MessageActivity.class, MessageScreen.instance(bookDetail.getBorrowerInfo().getUserId()));
        });
    }
    private void requestBookOwner(RequestStatusInput statusInput){
        getPresenter().requestChangeStatus(statusInput);
    }

    private void getBookDetailError(ServerResponse<ResponseData> error) {
        ClientUtils.showToast(error.message());
        if (error.code() == ServerResponse.HTTP_CODE.TOKEN) {
            MainActivity.start(this, StartActivity.class, LoginScreen.instance(Strings.EMPTY, true));
        }
    }

    private void getBookDetailSuccess(Data data) {
        if (data != null) {
            bookDetail = (BookDetailEntity) data.getDataReturn(BookDetailEntity.class);
            recordStatus = bookDetail.getRecordStatus();
            updateView();
        }
    }

    private void requestDetailBook(){
        getPresenter().requestBookDetail(borrowingRecordId);
    }

    private void requestBookByOwnerSuccess(ChangeStatusResponse data) {
        if (data != null) {
            if(data.getStatusCode() == 200){
                initView();
                requestDetailBook();
            }
            ClientUtils.showToast(data.getMessage());
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

        BookDetailEntity.BorrowerInfo borrowerInfo = bookDetail.getBorrowerInfo();
        if(borrowerInfo != null) {
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
            txtNumberSharing.setText(borrowerInfo.getSharingCount()+"");
            txtNumberReading.setText(borrowerInfo.getReadCount()+"");
        }
        ClientUtils.showToast("Record status:"+recordStatus);
        switch (recordStatus){
            case 0:
                //wait to confirm
                layoutSendRequest.setVisibility(View.VISIBLE);
                txtDateSendRequest.setText(ClientUtils.getDateFromString(bookDetail.getRequestTime()));
                layoutParrentAgreed.setVisibility(View.VISIBLE);
                layoutParrentReject.setVisibility(View.VISIBLE);
                layoutBottomLeft.setVisibility(View.GONE);
                break;
            case 1:
                //on hold - not change status
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
                layoutParrentBorrowBook.setVisibility(View.VISIBLE);
                layoutBorrowBook.setBackground(getResources().getDrawable(R.drawable.bg_layout_filter_book_return));
                layoutParrentReturnBook.setVisibility(View.VISIBLE);
                layoutParrentCancleRequest.setVisibility(View.VISIBLE);

                layoutBottomLeft.setVisibility(View.VISIBLE);
                break;
            case 3:
                //borrowing - can change status
                layoutSendRequest.setVisibility(View.VISIBLE);
                layoutStartBorrow.setVisibility(View.VISIBLE);
                txtDateSendRequest.setText(ClientUtils.getDateFromString(bookDetail.getRequestTime()));
                txtDateStartBorrow.setText(ClientUtils.getDateFromString(bookDetail.getBorrowTime()));

                layoutParrentContacting.setVisibility(View.VISIBLE);
                layoutParrentBorrowBook.setVisibility(View.VISIBLE);
                layoutContactingBorder.setVisibility(View.VISIBLE);
                layoutBorrowBookBorder.setVisibility(View.VISIBLE);
                layoutParrentReturnBook.setVisibility(View.VISIBLE);
                layoutParrentLostBook.setVisibility(View.VISIBLE);

                layoutBottomLeft.setVisibility(View.VISIBLE);
                rltOverLayBorrow.setVisibility(View.INVISIBLE);
                break;
            case 4:
                //completted - can't change status
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
                //reject - can't change status
                layoutSendRequest.setVisibility(View.VISIBLE);
                txtDateSendRequest.setText(ClientUtils.getDateFromString(bookDetail.getRequestTime()));
                layoutRejectRequest.setVisibility(View.VISIBLE);
                txtDateRejectRequest.setText(ClientUtils.getDateFromString(bookDetail.getRejectTime()));
                layoutParrentRejectedRequest.setVisibility(View.VISIBLE);
                layoutBottomLeft.setVisibility(View.GONE);
                break;
            case 6:
                //cancled - can't change status
                layoutSendRequest.setVisibility(View.VISIBLE);
                txtDateSendRequest.setText(ClientUtils.getDateFromString(bookDetail.getRequestTime()));
                layoutDeletedRequest.setVisibility(View.VISIBLE);
                txtDateDeleteRequest.setText(ClientUtils.getDateFromString(bookDetail.getRejectTime()));
                layoutBottomLeft.setVisibility(View.GONE);
                layoutParrentCancleRequest.setVisibility(View.VISIBLE);
                rltDeleteRequestClose.setVisibility(View.VISIBLE);
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
    protected Class<BookDetailOwnerPresenter> getPresenterClass() {
        return BookDetailOwnerPresenter.class;
    }
    @Override
    protected BookDetailOwnerScreen getDefaultScreen() {
        // TODO return null here
        return BookDetailOwnerScreen.instance(0);
    }
}
