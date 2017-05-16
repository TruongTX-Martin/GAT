package com.gat.feature.personal;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gat.R;
import com.gat.app.fragment.ScreenFragment;
import com.gat.common.adapter.ViewPagerAdapter;
import com.gat.common.event.NetWorkEvent;
import com.gat.common.util.ClientUtils;
import com.gat.common.util.Constance;
import com.gat.common.util.DataLocal;
import com.gat.common.util.Strings;
import com.gat.common.view.NonSwipeableViewPager;
import com.gat.data.response.ResponseData;
import com.gat.data.response.ServerResponse;
import com.gat.data.user.PaperUserDataSource;
import com.gat.feature.bookdetailsender.entity.ChangeStatusResponse;
import com.gat.feature.editinfo.EditInfoActivity;
import com.gat.feature.login.LoginActivity;
import com.gat.feature.login.LoginScreen;
import com.gat.feature.main.MainActivity;
import com.gat.feature.personal.entity.BookChangeStatusInput;
import com.gat.feature.personal.entity.BookInstanceInput;
import com.gat.feature.personal.entity.BookReadingInput;
import com.gat.feature.personal.entity.BookRequestInput;
import com.gat.feature.personal.entity.RequestStatusInput;
import com.gat.feature.personal.fragment.FragmentBookRequest;
import com.gat.feature.personal.fragment.FragmentBookSharing;
import com.gat.feature.personal.fragment.FragmentReadingBook;
import com.gat.feature.start.StartActivity;
import com.gat.repository.entity.Data;
import com.gat.repository.entity.User;
import com.gat.repository.entity.UsuallyLocation;
import com.gat.repository.entity.book.BookReadingEntity;
import com.gat.repository.entity.book.BookRequestEntity;
import com.gat.repository.entity.book.BookSharingEntity;
import com.google.gson.Gson;

import java.util.List;

import de.greenrobot.event.EventBus;
import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by root on 17/04/2017.
 */

public class PersonalFragment extends ScreenFragment<PersonalScreen, PersonalPresenter> {

    private CircleImageView imgAvatar;
    private TextView txtName;
    private TextView txtAddress;
    private TabLayout tabLayout;
    private NonSwipeableViewPager viewPager;
    private RelativeLayout layoutInfo;


    private RelativeLayout layoutTop;
    private ImageView imgBack, imgSave;
    private TextView txtTitle;

    private CompositeDisposable disposablesPersonal;
    private CompositeDisposable disposablesBookInstance;
    private CompositeDisposable disposablesChangeBookSharingStatus;
    private CompositeDisposable disposablesReadingBooks;
    private CompositeDisposable disposablesBooksRequest;
    private CompositeDisposable disposablesRequestBookByOwner;
    private CompositeDisposable disposablesCheckLogin;
    private CompositeDisposable disposablesRemoveBook;

    //init fragment
    private FragmentBookSharing fragmentBookSharing;
    private FragmentReadingBook fragmentBookReading;
    private FragmentBookRequest fragmentBookRequest;
    private TextView txtNumberSharing;
    private TextView txtNumberReading;
    private TextView txtNumberRequest;
//    private BookRequestInput bookRequestInput = new BookRequestInput(true,true,true,true);

    private User userInfo;
    private Context context;
    private View rootView;

    private MainActivity mainActivity;
    Dialog dialog;

    public void setMainActivity(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    public void setTabDesire(int position) {
        tabLayout.setScrollPosition(position,0f,true);
        viewPager.setCurrentItem(position);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.layout_personal_activity;
    }

    @Override
    protected Class<PersonalPresenter> getPresenterClass() {
        return PersonalPresenter.class;
    }


    @Override
    protected PersonalScreen getDefaultScreen() {
        return PersonalScreen.instance();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = super.onCreateView(inflater, container, savedInstanceState);
        context = getActivity().getApplicationContext();
        rootView = inflater.inflate(R.layout.layout_personal_activity,
                container, false);

        disposablesPersonal = new CompositeDisposable(getPresenter().getResponsePersonal().subscribe(this::getUserInfoSuccess),
                getPresenter().onErrorPersonal().subscribe(this::getUserInfoError));

        disposablesBookInstance = new CompositeDisposable(getPresenter().getResponseBookInstance().subscribe(this::getBookInstanceSuccess),
                getPresenter().onErrorBookInstance().subscribe(this::getBookInstanceError));


        disposablesChangeBookSharingStatus = new CompositeDisposable(getPresenter().getResponseBookSharingStatus().subscribe(this::changeBookSharingStatusSuccess),
                getPresenter().onErrorBookSharingStatus().subscribe(this::changeStatusBookError));

        disposablesReadingBooks = new CompositeDisposable(getPresenter().getResponseReadingBooks().subscribe(this::getReadingBooksSuccess),
                getPresenter().onErrorReadingBooks().subscribe(this::getBookInstanceError));

        disposablesBooksRequest = new CompositeDisposable(getPresenter().getResponseBookRequest().subscribe(this::getBookRequestSuccess),
                getPresenter().onErrorBookRequest().subscribe(this::getBookInstanceError));

        disposablesRequestBookByOwner = new CompositeDisposable(getPresenter().getResponseChangeStatus().subscribe(this::requestBookByOwnerSuccess),
                getPresenter().onErrorChangeStatus().subscribe(this::checkLoginFailed));
        disposablesCheckLogin = new CompositeDisposable(getPresenter().checkLoginSucess().subscribe(this::checkLoginSuccess),
                getPresenter().checkLoginFailed().subscribe(this::checkLoginFailed));

        disposablesRemoveBook = new CompositeDisposable(getPresenter().getResponseRemoveBook().subscribe(this::removeBookSuccess),
                getPresenter().onErrorRemoveBook().subscribe(this::checkLoginFailed));

        initView();

        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();

        handleEvent();
        return rootView;
    }


//    public void isConnected(boolean isConnected) {
//        if(isConnected) {
//            ClientUtils.hideViewNotInternet(layoutTop);
//        }else{
//            ClientUtils.showViewNotInternet(layoutTop);
//        }
//    }

    private void initView() {
        viewPager = (NonSwipeableViewPager) rootView.findViewById(R.id.viewpager);
        tabLayout = (TabLayout) rootView.findViewById(R.id.tabLayout);
        imgAvatar = (CircleImageView) rootView.findViewById(R.id.imgAvatar);
        txtName = (TextView) rootView.findViewById(R.id.txtName);
        txtAddress = (TextView) rootView.findViewById(R.id.txtAddress);
        layoutInfo = (RelativeLayout) rootView.findViewById(R.id.layoutEdit);

        layoutTop = (RelativeLayout) rootView.findViewById(R.id.layoutMenutop);
        imgBack = (ImageView) rootView.findViewById(R.id.imgBack);
        imgSave = (ImageView) rootView.findViewById(R.id.imgSave);
        txtTitle = (TextView) rootView.findViewById(R.id.txtTitle);
        imgBack.setVisibility(View.GONE);
        imgSave.setVisibility(View.GONE);
        txtTitle.setText("CÁ NHÂN");
        txtTitle.setTextColor(Color.parseColor("#ffffff"));
        layoutTop.setBackgroundColor(Color.parseColor("#8ec3df"));

        dialog = new Dialog(MainActivity.instance);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_dialog_login);
        dialog.setCanceledOnTouchOutside(false);
    }

    private void handleEvent() {
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                tabLayout.setSelectedTabIndicatorColor(Color.parseColor("#f6f6f6"));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        layoutInfo.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.instance, EditInfoActivity.class);
            intent.putExtra("UserInfo", userInfo);
            MainActivity.instance.startActivityForResult(intent, Constance.RESULT_UPDATEUSER);
        });
    }

    private void setupTabIcons() {

        View tabOne = LayoutInflater.from(mContext).inflate(R.layout.layout_tab_book, null);
        ImageView imgTabOne = (ImageView) tabOne.findViewById(R.id.imgCircle);
        imgTabOne.setImageDrawable(getResources().getDrawable(R.drawable.ic_circle_loanbook));
        txtNumberSharing = (TextView) tabOne.findViewById(R.id.txtNumber);
        txtNumberSharing.setText("0");
        TextView txtTitleOne = (TextView) tabOne.findViewById(R.id.txtTitle);
        txtTitleOne.setText("Sách cho mượn");
        tabLayout.getTabAt(0).setCustomView(tabOne);

        View tabTwo = LayoutInflater.from(mContext).inflate(R.layout.layout_tab_book, null);
        ImageView imgTabTwo = (ImageView) tabTwo.findViewById(R.id.imgCircle);
        imgTabTwo.setImageDrawable(getResources().getDrawable(R.drawable.ic_circle_readingbook));
        txtNumberReading = (TextView) tabTwo.findViewById(R.id.txtNumber);
        txtNumberReading.setText("0");
        TextView txtTitleTwo = (TextView) tabTwo.findViewById(R.id.txtTitle);
        txtTitleTwo.setText("Sách đang đọc");
        tabLayout.getTabAt(1).setCustomView(tabTwo);
//
        View tabThree = LayoutInflater.from(mContext).inflate(R.layout.layout_tab_book, null);
        ImageView imgTabThree = (ImageView) tabThree.findViewById(R.id.imgCircle);
        imgTabThree.setImageDrawable(getResources().getDrawable(R.drawable.ic_circle_requestbook));
        txtNumberRequest = (TextView) tabThree.findViewById(R.id.txtNumber);
        txtNumberRequest.setText("0");
        TextView txtTitleThree = (TextView) tabThree.findViewById(R.id.txtTitle);
        txtTitleThree.setText("Yêu cầu mượn sách");
        tabLayout.getTabAt(2).setCustomView(tabThree);
        tabLayout.setSelectedTabIndicatorHeight(0);
    }


    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(MainActivity.instance.getSupportFragmentManager());
        if (fragmentBookSharing == null) {
            fragmentBookSharing = new FragmentBookSharing();
            fragmentBookSharing.setParrentActivity(this);
        }
        if (fragmentBookReading == null) {
            fragmentBookReading = new FragmentReadingBook();
            fragmentBookReading.setParrentActivity(this);
        }
        if (fragmentBookRequest == null) {
            fragmentBookRequest = new FragmentBookRequest();
            fragmentBookRequest.setParrentFragment(this);
        }
        adapter.addFragment(fragmentBookSharing, "");
        adapter.addFragment(fragmentBookReading, "");
        adapter.addFragment(fragmentBookRequest, "");
        viewPager.setAdapter(adapter);
    }

    public void requestPersonalInfo() {
        getPresenter().requestPersonalInfor("");
    }

    //handle data personal return
    private void getUserInfoSuccess(Data<User> data) {
        if (data != null) {
            userInfo = data.getDataReturn(User.typeAdapter(new Gson()));
            if (userInfo == null)
                userInfo = User.NONE;
            if (!Strings.isNullOrEmpty(userInfo.name())) {
                txtName.setText(userInfo.name());
                txtAddress.setText(userInfo.name());
            }
            if(userInfo.usuallyLocation().size() > 0){
                if(!Strings.isNullOrEmpty(userInfo.usuallyLocation().get(0).getAddress())){
                    txtAddress.setText(userInfo.usuallyLocation().get(0).getAddress());
                }
            }
            if (!Strings.isNullOrEmpty(userInfo.imageId())) {
                String url = ClientUtils.getUrlImage(userInfo.imageId(), Constance.IMAGE_SIZE_ORIGINAL);
                ClientUtils.setImage(imgAvatar, R.drawable.ic_profile, url);
            }
            if (/*userInfo.userId() > 0*/userInfo.isValid()) {
                BookReadingInput readingInput = new BookReadingInput(true, false, false);
                if(DataLocal.getPersonalInputReading() != null) {
                    readingInput = DataLocal.getPersonalInputReading();
                }
                readingInput.setUserId(userInfo.userId());
                requestReadingBooks(readingInput);
                BookInstanceInput currentInput = new BookInstanceInput(true, false, false);
                if (DataLocal.getPersonalInputSharing() != null) {
                    currentInput = DataLocal.getPersonalInputSharing();
                }
                fragmentBookSharing.setCurrentInput(currentInput);
                requestBookInstance(currentInput);
            }
        }
    }

    private void getBookDetailError(String error) {
//        ClientUtils.showToast(getActivity(), "Error:" + error);
    }

    private void getUserInfoError(ServerResponse<ResponseData> error) {
        ClientUtils.showDialogError(MainActivity.instance,ClientUtils.getStringLanguage(R.string.titleError),error.message());
    }

    public void requestBookOwner(RequestStatusInput statusInput) {
        getPresenter().requestChangeStatus(statusInput);
    }

    private void requestBookByOwnerSuccess(ChangeStatusResponse data) {
        if (data != null) {
//            ClientUtils.showToast(getActivity(), data.getMessage());
            fragmentBookRequest.refreshAdapter();
        }
    }

    public void checkLogin() {
        getPresenter().checkLogin();
    }

    private void checkLoginSuccess(String input) {
        //do nothing
    }

    private void removeBookSuccess(String data){
        fragmentBookSharing.updateAfterDelete();
        
    }

    public void removeBookInstance(int instanceId){
        getPresenter().removeBook(instanceId);
    }


    private void checkLoginFailed(String input) {
        //show dialog
        Button btnCancle = (Button) dialog.findViewById(R.id.btnCancle);
        Button btnOk = (Button) dialog.findViewById(R.id.btnOk);
        btnCancle.setOnClickListener(v -> {
            mainActivity.setTabDesire(0);
            dialog.dismiss();
        });
        btnOk.setOnClickListener(v -> {
            MainActivity.start(MainActivity.instance.getApplicationContext(), LoginActivity.class, LoginScreen.instance(Strings.EMPTY));
        });
        if(dialog != null && !dialog.isShowing()){
            dialog.show();
        }
    }

    //get book instance
    public void requestBookInstance(BookInstanceInput input) {
        if (!ClientUtils.isOnline()) {
            ClientUtils.showViewNotInternet(MainActivity.instance,layoutTop);
            return;
        }
        getPresenter().requestBookInstance(input);
    }

    //change status book
    public void requestChangeStatusBook(BookChangeStatusInput input) {
        getPresenter().requestChangeBookSharingStatus(input);
    }

    //request reading book
    public void requestReadingBooks(BookReadingInput input) {
        getPresenter().requestReadingBooks(input);
        fragmentBookReading.setCurrentInput(input);
        DataLocal.savePersonalInputReading(input);
    }

    //request bookrequest
    public void requestBookRequest(BookRequestInput input) {
        getPresenter().requestBookRequests(input);
        DataLocal.savePersonalInputRequest(input);
    }

    //handle get bookInstance return
    private void getBookInstanceSuccess(Data data) {
        if (data != null) {
            int totalSharing = data.getTotalSharing();
            txtNumberSharing.setText(totalSharing + "");
            int totalNotSharing = data.getTotalNotSharing();
            int lostTotal = data.getLostTotal();
            List<BookSharingEntity> listBook = data.getListDataReturn(BookSharingEntity.class);
            fragmentBookSharing.setNumberSharing(totalSharing);
            fragmentBookSharing.setNumberNotSharing(totalNotSharing);
            fragmentBookSharing.setNumberLost(lostTotal);
            fragmentBookSharing.setListBook(listBook);
        }
    }

    private void getBookInstanceError(ServerResponse<ResponseData> error) {
        if (error.code() == ServerResponse.HTTP_CODE.TOKEN) {
            MainActivity.start(getActivity(), StartActivity.class, LoginScreen.instance(Strings.EMPTY, true));
        }else{
            ClientUtils.showDialogError(MainActivity.instance,ClientUtils.getStringLanguage(R.string.titleError),error.message());
        }
    }

    private void changeStatusBookError(String error) {
        ClientUtils.showDialogError(MainActivity.instance,ClientUtils.getStringLanguage(R.string.titleError),error);
    }


    private void changeBookSharingStatusSuccess(String data) {
        if (!Strings.isNullOrEmpty(data)) {
//            fragmentBookSharing.refreshDate();
        }
    }

    private void getReadingBooksSuccess(Data data) {
        if (data != null) {
            int totalReading = data.getReadingTotal();
            int totalReaded = data.getReadTotal();
            int totalToRead = data.getToReadTotal();
            if (totalReading > 0) {
                txtNumberReading.setText(totalReading + "");
            }
            List<BookReadingEntity> listReading = data.getListDataReturn(BookReadingEntity.class);
            fragmentBookReading.setNumberReading(totalReading);
            fragmentBookReading.setNumberReaded(totalReaded);
            fragmentBookReading.setNumberToRead(totalToRead);
            fragmentBookReading.setListBookReading(listReading);
        }

//        requestBookRequest(bookRequestInput);
//        fragmentBookRequest.setCurrentInput(bookRequestInput);
    }

    private void getBookRequestSuccess(Data data) {
        if (data != null) {
            int totalBrowing = data.getBorrowingTotal();
            int totalSharing = data.getTotalSharing();
            txtNumberRequest.setText(totalSharing + "");
            List<BookRequestEntity> listBookRequest = data.getListDataReturn(BookRequestEntity.class);
            fragmentBookRequest.setNumberRequestFromYou(totalBrowing);
            fragmentBookRequest.setNumberRequestToYou(totalSharing);
            fragmentBookRequest.setListBookRequest(listBookRequest);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        disposablesPersonal.dispose();
        disposablesBookInstance.dispose();
        disposablesChangeBookSharingStatus.dispose();
        disposablesReadingBooks.dispose();
        disposablesBooksRequest.dispose();
        disposablesRequestBookByOwner.dispose();
        disposablesCheckLogin.dispose();
        EventBus.getDefault().unregister(this);
    }

}
