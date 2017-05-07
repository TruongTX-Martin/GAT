package com.gat.feature.personaluser;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gat.R;
import com.gat.app.activity.ScreenActivity;
import com.gat.common.adapter.ViewPagerAdapter;
import com.gat.common.util.ClientUtils;
import com.gat.common.util.Constance;
import com.gat.common.util.Strings;
import com.gat.common.view.NonSwipeableViewPager;
import com.gat.data.response.ResponseData;
import com.gat.data.response.ServerResponse;
import com.gat.data.response.UserResponse;
import com.gat.feature.personal.entity.BookReadingInput;
import com.gat.feature.personaluser.entity.BookSharingUserInput;
import com.gat.feature.personaluser.entity.BorrowRequestInput;
import com.gat.feature.personaluser.fragment.FragmentBookUserReading;
import com.gat.feature.personaluser.fragment.FragmentBookUserSharing;
import com.gat.repository.entity.Data;
import com.gat.repository.entity.book.BookReadingEntity;
import com.gat.repository.entity.book.BookSharingEntity;

import java.util.List;

import butterknife.BindView;
import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by root on 20/04/2017.
 */

public class PersonalUserActivity extends ScreenActivity<PersonalUserScreen, PersonalUserPresenter> {

    UserResponse userResponse;

    @BindView(R.id.txtName)
    TextView txtName;

    @BindView(R.id.txtAddress)
    TextView txtAddress;

    @BindView(R.id.tabLayout)
    TabLayout tabLayout;

    @BindView(R.id.viewpager)
    NonSwipeableViewPager viewPager;

    @BindView(R.id.imgAvatar)
    CircleImageView imgAvatar;

    @BindView(R.id.imgBack)
    ImageView imgBack;

    @BindView(R.id.imgSave)
    ImageView imgChat;

    @BindView(R.id.txtTitle)
    TextView txtTitle;

    @BindView(R.id.layoutMenutop)
    RelativeLayout layoutMenutop;

    private Context context;


    //init fragment
    private FragmentBookUserSharing fragmentBookUserSharing;
    private FragmentBookUserReading fragmentBookUserReading;
    private TextView txtNumberSharing,txtNumberReading;


    private CompositeDisposable disposablesBookUserSharing;
    private CompositeDisposable disposablesBookUserReading;
    private CompositeDisposable disposableBorrowBook;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userResponse = getScreen().userResponse();
        context = getApplicationContext();
        disposablesBookUserSharing = new CompositeDisposable(getPresenter().getResponseBookUserSharing().subscribe(this::getBookUserSharingSuccess),
                getPresenter().onErrorBookUserSharing().subscribe(this::getBookUserSharingError));

        disposablesBookUserReading = new CompositeDisposable(getPresenter().getResponseBookUserReading().subscribe(this::getBookUserReadingSuccess),
                getPresenter().onErrorBookUserReading().subscribe(this::getBookUserSharingError));

        disposableBorrowBook = new CompositeDisposable(getPresenter().getResponseBorrowBook().subscribe(this::borrowBookSuccess),
                getPresenter().onErrorBorrowBook().subscribe(this::borrowBookError));


        initView();
        handleEvent();
    }

    private void initView() {
        layoutMenutop.setBackgroundColor(Color.parseColor("#8ec3df"));
        txtTitle.setText("CÁ NHÂN");
        imgChat.setImageResource(R.drawable.ic_chat_white);
        if(userResponse != null) {
            if(!Strings.isNullOrEmpty(userResponse.getName())) {
                txtName.setText(userResponse.getName());
            }
            if(!Strings.isNullOrEmpty(userResponse.getAddress())) {
                txtAddress.setText(userResponse.getAddress());
            }
            if (!Strings.isNullOrEmpty(userResponse.getImageId())) {
                String url = ClientUtils.getUrlImage(userResponse.getImageId(), Constance.IMAGE_SIZE_ORIGINAL);
                ClientUtils.setImage(imgAvatar, R.drawable.ic_profile, url);
            }
        }

        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();
    }

    private void handleEvent(){
        imgBack.setOnClickListener(v -> finish());

        //event chating
        imgChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void setupTabIcons() {
        View tabOne = LayoutInflater.from(context).inflate(R.layout.layout_tab_book, null);
        ImageView imgTabOne = (ImageView) tabOne.findViewById(R.id.imgCircle);
        imgTabOne.setImageDrawable(getResources().getDrawable(R.drawable.ic_circle_loanbook));
        txtNumberSharing = (TextView) tabOne.findViewById(R.id.txtNumber);
        txtNumberSharing.setText("0");
        TextView txtTitleOne = (TextView) tabOne.findViewById(R.id.txtTitle);
        txtTitleOne.setText("Sách cho mượn");
        tabLayout.getTabAt(0).setCustomView(tabOne);

        View tabTwo = LayoutInflater.from(context).inflate(R.layout.layout_tab_book, null);
        ImageView imgTabTwo = (ImageView) tabTwo.findViewById(R.id.imgCircle);
        imgTabTwo.setImageDrawable(getResources().getDrawable(R.drawable.ic_circle_readingbook));
        txtNumberReading = (TextView) tabTwo.findViewById(R.id.txtNumber);
        txtNumberReading.setText("0");
        TextView txtTitleTwo = (TextView) tabTwo.findViewById(R.id.txtTitle);
        txtTitleTwo.setText("Sách đang đọc");
        tabLayout.getTabAt(1).setCustomView(tabTwo);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        if (fragmentBookUserSharing == null) {
            fragmentBookUserSharing = new FragmentBookUserSharing();
            fragmentBookUserSharing.setParrentActivity(this);
            try {
                BookSharingUserInput input = new BookSharingUserInput();
                input.setOwnerId(userResponse.getUserId());
                fragmentBookUserSharing.setCurrentInput(input);
            }catch (Exception e){}
        }
        if (fragmentBookUserReading == null) {
            fragmentBookUserReading = new FragmentBookUserReading();
            fragmentBookUserReading.setParrentActivity(this);
            try {
                BookReadingInput currentInput = new BookReadingInput(false,true,false);
                currentInput.setUserId(userResponse.getUserId());
                fragmentBookUserReading.setCurrentInput(currentInput);
            }catch (Exception e){}
        }
        adapter.addFragment(fragmentBookUserSharing, "");
        adapter.addFragment(fragmentBookUserReading, "");
        viewPager.setAdapter(adapter);
    }

    public void requestBookUserSharing(BookSharingUserInput input){
        getPresenter().requestBookUserSharing(input);
    }

    private void getBookUserSharingSuccess(Data data){
        if(data != null){
            int totalSharing = data.getTotalSharing();
            txtNumberSharing.setText(totalSharing + "");
            List<BookSharingEntity> list = data.getListDataReturn(BookSharingEntity.class);
            fragmentBookUserSharing.setListBook(list);
        }
    }

    private void getBookUserSharingError(ServerResponse<ResponseData> error) {
        ClientUtils.showToast(error.message());
    }

    private void borrowBookError(String  error) {
        ClientUtils.showToast(error);
    }

    private void borrowBookSuccess(Data data){
        if(data != null) {
            if (!Strings.isNullOrEmpty(data.getMessage())) {
                ClientUtils.showToast(data.getMessage());
            }
            BookSharingEntity entity = (BookSharingEntity) data.getDataReturn(BookSharingEntity.class);
            if (entity != null) {
                int editionId = entity.getEditionId();
                fragmentBookUserSharing.refreshAdapterSharingBook(editionId);
            }
        }
    }
    private void getBookUserReadingSuccess(Data data){
        if(data != null){
            List<BookReadingEntity> list = data.getListDataReturn(BookReadingEntity.class);
            fragmentBookUserReading.setListBook(list);
            int totalReading = data.getReadingTotal();
            txtNumberReading.setText(totalReading+"");
        }
    }

    public void requestBorrowBook(BorrowRequestInput input){
        input.setOwnerId(userResponse.getUserId());
        getPresenter().requestBorrowBook(input);
    }



    public void requestBookUserReading(BookReadingInput input){
        getPresenter().requestBookUserReading(input);
    }


    @Override
    protected int getLayoutResource() {
        return R.layout.layout_personaluser_activity;
    }

    @Override
    protected Class<PersonalUserPresenter> getPresenterClass() {
        return PersonalUserPresenter.class;
    }

    @Override
    protected PersonalUserScreen getDefaultScreen() {
        return PersonalUserScreen.instance(getScreen().userResponse());
    }

    @Override
    protected Object getPresenterKey() {
        return PersonalUserScreen.instance(getScreen().userResponse());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposablesBookUserSharing.dispose();
        disposablesBookUserReading.dispose();
        disposableBorrowBook.dispose();
    }
}
