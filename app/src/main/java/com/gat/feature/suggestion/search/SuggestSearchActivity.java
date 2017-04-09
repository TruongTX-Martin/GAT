package com.gat.feature.suggestion.search;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;

import com.gat.R;
import com.gat.app.activity.ScreenActivity;
import com.gat.common.adapter.ViewPagerAdapter;
import com.gat.feature.main.MainPresenter;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by mryit on 4/9/2017.
 */

public class SuggestSearchActivity extends ScreenActivity<SuggestSearchScreen, MainPresenter> {

    @BindView(R.id.tab_layout)
    TabLayout mTabLayout;

    @BindView(R.id.view_pager)
    ViewPager mViewPager;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_search;
    }

    @Override
    protected Class<MainPresenter> getPresenterClass() {
        return MainPresenter.class;
    }

    @Override
    protected SuggestSearchScreen getDefaultScreen() {
        return SuggestSearchScreen.instance();
    }

    @OnClick(R.id.button_cancel)
    void onButtonCancelTap () {
        finish();
        overridePendingTransition( R.anim.no_change, R.anim.push_down );
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupViewPager(mViewPager);
        mTabLayout.setupWithViewPager(mViewPager);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new SearchBookFragment(), "Tên sách");
        adapter.addFragment(new SearchAuthorFragment(), "Tác giả");
        adapter.addFragment(new SearchUserFragment(), "Người dùng");
        viewPager.setAdapter(adapter);
    }

}
