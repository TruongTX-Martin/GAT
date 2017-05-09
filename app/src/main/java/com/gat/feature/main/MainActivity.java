package com.gat.feature.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;

import com.gat.R;
import com.gat.app.activity.ScreenActivity;
import com.gat.common.adapter.ViewPagerAdapter;
import com.gat.common.util.ClientUtils;
import com.gat.common.util.CommonCheck;
import com.gat.common.view.NonSwipeableViewPager;
import com.gat.common.util.NotificationConfig;
import com.gat.data.firebase.entity.Notification;
import com.gat.data.firebase.entity.NotificationParcelable;
import com.gat.feature.message.MessageActivity;
import com.gat.feature.message.presenter.MessagePresenter;
import com.gat.feature.message.presenter.MessageScreen;
import com.gat.feature.notification.NotificationFragment;
import com.gat.common.util.Constance;
import com.gat.feature.personal.PersonalFragment;
import com.gat.feature.scanbarcode.ScanFragment;
import com.gat.feature.setting.SettingFragment;
import com.gat.feature.setting.main.MainSettingFragment;
import com.gat.feature.suggestion.SuggestionFragment;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

import butterknife.BindView;

/**
 * Created by mryit on 3/26/2017.
 */

public class MainActivity extends ScreenActivity<MainScreen, MainPresenter> {

    @Retention(RetentionPolicy.SOURCE)
    public @interface TAB_POS {
        int TAB_HOME            = 0;
        int TAB_PERSONAL        = 1;
        int TAB_SCAN            = 2;
        int TAB_NOTIFICATION    = 3;
        int TAB_SETTING         = 4;
    }

    @BindView(R.id.viewPager)
    NonSwipeableViewPager mViewPager;

    @BindView(R.id.tabLayout)
    TabLayout mTabLayout;

    public static MainActivity instance;
    private PersonalFragment personalFragment;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_main;
    }

    @Override
    protected MainScreen getDefaultScreen() {
        return MainScreen.instance();
    }

    @Override
    protected Class<MainPresenter> getPresenterClass() {
        return MainPresenter.class;
    }

    @Override
    protected Object getPresenterKey() {
        return MainScreen.instance();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ClientUtils.context = getApplicationContext();
        instance = this;

        // setup view pager
        setupViewPager(mViewPager);
        // setup tab layout
        mTabLayout.setupWithViewPager(mViewPager);
        setupTabLayoutIcons(mTabLayout);
        // set up icon high light
        mTabLayout.getTabAt(TAB_POS.TAB_HOME).setIcon(R.drawable.home_ic_selected);
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                selectTab(tab, true);// For refresh layout
                mTabLayout.setScrollPosition(tab.getPosition(), 0, true);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                selectTab(tab, false);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        Intent intent = getIntent();
        if (intent != null) {
            NotificationParcelable parcelable = intent.getExtras().getParcelable("data");
            if (parcelable != null)
                CommonCheck.processNotification(parcelable.getNotification(), this);
        }
        if (Notification.isValid(getScreen().notificationParcelable().getNotification())) {
            CommonCheck.processNotification(getScreen().notificationParcelable().getNotification(), this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        // your code.
    }

    private void setupViewPager(ViewPager viewPager) {
        personalFragment = new PersonalFragment();
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new SuggestionFragment(), "HOME PAGE");
        adapter.addFragment(personalFragment, "PERSONAL");
        adapter.addFragment(new ScanFragment(), "SCAN");
        adapter.addFragment(new NotificationFragment(), "NOTICE");
        adapter.addFragment(new SettingFragment(), "SETTING");
        viewPager.setOffscreenPageLimit(4);
        viewPager.setAdapter(adapter);
    }

    private void setupTabLayoutIcons (TabLayout tabLayout) {
        tabLayout.getTabAt(TAB_POS.TAB_HOME).setIcon(R.drawable.home_ic);
        tabLayout.getTabAt(TAB_POS.TAB_PERSONAL).setIcon(R.drawable.personal_ic);
        tabLayout.getTabAt(TAB_POS.TAB_SCAN).setIcon(R.drawable.scan_ic);
        tabLayout.getTabAt(TAB_POS.TAB_NOTIFICATION).setIcon(R.drawable.notic_ic);
        tabLayout.getTabAt(TAB_POS.TAB_SETTING).setIcon(R.drawable.setting_ic);
    }

    private void selectTab(TabLayout.Tab tab, boolean select) {
        switch (tab.getPosition()) {
            case TAB_POS.TAB_HOME:
                tab.setIcon(select ? R.drawable.home_ic_selected : R.drawable.home_ic);
                break;
            case TAB_POS.TAB_PERSONAL:
                tab.setIcon(select ? R.drawable.personal_ic_selected : R.drawable.personal_ic);
                break;
            case TAB_POS.TAB_SCAN:
                tab.setIcon(select ? R.drawable.scan_ic_selected : R.drawable.scan_ic);
                break;
            case TAB_POS.TAB_NOTIFICATION:
                tab.setIcon(select ? R.drawable.notic_ic_selected : R.drawable.notic_ic);
                break;
            case TAB_POS.TAB_SETTING:
                tab.setIcon(select ? R.drawable.setting_ic_selected : R.drawable.setting_ic);
                break;
            default:
                throw new UnsupportedOperationException();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == Constance.RESULT_UPDATEUSER){
            personalFragment.requestPersonalInfo();
        }

        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        if (fragments != null) {
            for (Fragment fragment : fragments) {
                if(fragment instanceof MainSettingFragment) {
                    fragment.onActivityResult(requestCode, resultCode, data);
                    break;
                }
            }
        }

    }
}
