package com.gat.feature.personal;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gat.R;

import com.gat.app.activity.ScreenActivity;
import com.gat.common.util.ClientUtils;
import com.gat.data.response.ResponseData;
import com.gat.data.response.ServerResponse;
import com.gat.feature.personal.fragment.FragmentBookRequest;
import com.gat.feature.personal.fragment.FragmentLoanBook;
import com.gat.feature.personal.fragment.FragmentReadingBook;
import com.gat.repository.entity.Data;
import com.gat.repository.entity.UserInfo;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by truongtechno on 23/03/2017.
 */

public class PersonalActivity extends ScreenActivity<PersonalScreen,PersonalPresenter> {

    @BindView(R.id.imgAvatar)
    ImageView imgAvatar;

    @BindView(R.id.txtName)
    TextView txtName;

    @BindView(R.id.txtAddress)
    TextView txtAddress;

    @BindView(R.id.tabLayout)
    TabLayout tabLayout;

    @BindView(R.id.viewpager)
    ViewPager viewPager;

    private CompositeDisposable disposables;
    @Override
    protected PersonalScreen getDefaultScreen() {
        return PersonalScreen.instance();
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
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        disposables = new CompositeDisposable(getPresenter().getResponse().subscribe(this::getUserInfoSuccess),
                getPresenter().onError().subscribe(this::getUserInfoError));
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();
    }

    private void setupTabIcons() {

        View tabOne =  LayoutInflater.from(this).inflate(R.layout.layout_tab_book, null);
        ImageView imgTabOne = (ImageView) tabOne.findViewById(R.id.imgCircle);
        imgTabOne.setImageDrawable(getResources().getDrawable(R.drawable.ic_circle_loanbook));
        TextView txtNumberOne = (TextView) tabOne.findViewById(R.id.txtNumber);
        txtNumberOne.setText("6");
        TextView txtTitleOne = (TextView) tabOne.findViewById(R.id.txtTitle);
        txtTitleOne.setText("Sách cho mượn");
        tabLayout.getTabAt(0).setCustomView(tabOne);

        View tabTwo =  LayoutInflater.from(this).inflate(R.layout.layout_tab_book, null);
        ImageView imgTabTwo = (ImageView) tabTwo.findViewById(R.id.imgCircle);
        imgTabTwo.setImageDrawable(getResources().getDrawable(R.drawable.ic_circle_readingbook));
        TextView txtNumberTwo = (TextView) tabTwo.findViewById(R.id.txtNumber);
        txtNumberTwo.setText("5");
        TextView txtTitleTwo = (TextView) tabTwo.findViewById(R.id.txtTitle);
        txtTitleTwo.setText("Sách đang đọc");
        tabLayout.getTabAt(1).setCustomView(tabTwo);
//
        View tabThree =  LayoutInflater.from(this).inflate(R.layout.layout_tab_book, null);
        ImageView imgTabThree = (ImageView) tabThree.findViewById(R.id.imgCircle);
        imgTabThree.setImageDrawable(getResources().getDrawable(R.drawable.ic_circle_requestbook));
        TextView txtNumberThree = (TextView) tabThree.findViewById(R.id.txtNumber);
        txtNumberThree.setText("10");
        TextView txtTitleThree = (TextView) tabThree.findViewById(R.id.txtTitle);
        txtTitleThree.setText("Yêu cầu");
        tabLayout.getTabAt(2).setCustomView(tabThree);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new FragmentLoanBook(), "");
        adapter.addFrag(new FragmentReadingBook(), "");
        adapter.addFrag(new FragmentBookRequest(), "");
        viewPager.setAdapter(adapter);
    }
    private void getUserInfoSuccess(Data data){
        System.out.println(data);
        System.out.println("Get user info success");
        if(data != null){
            UserInfo userInfo = data.getUserInfo();
            if(userInfo != null && ClientUtils.validate(userInfo.getName())){
                txtName.setText(userInfo.getName());
            }
        }
    }
    private void getUserInfoError(ServerResponse<ResponseData> error) {
        Toast.makeText(this, error.message(), Toast.LENGTH_SHORT).show();
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        System.out.println("On Back");
    }
}
