package com.gat.feature.personaluser;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.gat.R;
import com.gat.app.activity.ScreenActivity;
import com.gat.common.adapter.ViewPagerAdapter;
import com.gat.common.util.ClientUtils;
import com.gat.common.util.Strings;
import com.gat.common.view.NonSwipeableViewPager;
import com.gat.data.response.ResponseData;
import com.gat.data.response.ServerResponse;
import com.gat.data.response.UserResponse;
import com.gat.feature.main.MainActivity;
import com.gat.feature.personal.PersonalFragment;
import com.gat.feature.personal.fragment.FragmentBookRequest;
import com.gat.feature.personal.fragment.FragmentBookSharing;
import com.gat.feature.personal.fragment.FragmentReadingBook;
import com.gat.feature.personaluser.entity.BookSharingUserInput;
import com.gat.feature.personaluser.fragment.FragmentBookUserReading;
import com.gat.feature.personaluser.fragment.FragmentBookUserSharing;
import com.gat.repository.entity.Data;
import com.gat.repository.entity.book.BookSharingEntity;

import java.util.List;

import butterknife.BindView;
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

    private Context context;


    //init fragment
    private FragmentBookUserSharing fragmentBookUserSharing;
    private FragmentBookUserReading fragmentBookUserReading;
    private TextView txtNumberSharing,txtNumberReading;


    private CompositeDisposable disposablesBookUserSharing;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userResponse = (UserResponse) getIntent().getSerializableExtra("UserInfo");
        context = getApplicationContext();
        disposablesBookUserSharing = new CompositeDisposable(getPresenter().getResponseBookUserSharing().subscribe(this::getBookUserSharingSuccess),
                getPresenter().onErrorBookUserSharing().subscribe(this::getBookUserSharingError));
        initView();
        requestBookUserSharing();
    }

    private void initView() {
        if(userResponse != null) {
            if(!Strings.isNullOrEmpty(userResponse.getName())) {
                txtName.setText(userResponse.getName());
            }
            if(!Strings.isNullOrEmpty(userResponse.getAddress())) {
                txtAddress.setText(userResponse.getAddress());
            }
        }
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();
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
        ViewPagerAdapter adapter = new ViewPagerAdapter(MainActivity.instance.getSupportFragmentManager());
        if (fragmentBookUserSharing == null) {
            fragmentBookUserSharing = new FragmentBookUserSharing();
            fragmentBookUserSharing.setParrentActivity(this);
        }
        if (fragmentBookUserReading == null) {
            fragmentBookUserReading = new FragmentBookUserReading();
        }
        adapter.addFragment(fragmentBookUserSharing, "");
        adapter.addFragment(fragmentBookUserReading, "");
        viewPager.setAdapter(adapter);
    }

    public void requestBookUserSharing(){
        BookSharingUserInput input = new BookSharingUserInput();
        input.setOwnerId((int)userResponse.getUserId());
        input.setUserId(56);
        getPresenter().requestBookUserSharing(input);
    }

    private void getBookUserSharingSuccess(Data data){
        if(data != null){
            List<BookSharingEntity> list = data.getListDataReturn(BookSharingEntity.class);
        }
    }

    private void getBookUserSharingError(ServerResponse<ResponseData> error) {
        ClientUtils.showToast(error.message());
    }



    public void requestBookUserReading(){

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
        return PersonalUserScreen.instance();
    }


}
