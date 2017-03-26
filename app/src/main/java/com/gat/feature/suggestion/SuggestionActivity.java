package com.gat.feature.suggestion;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.gat.R;
import com.gat.app.activity.ScreenActivity;
import com.gat.common.fragment.MainTabFragment;
import com.gat.common.util.Strings;
import com.gat.feature.scanbarcode.ScanBarcodeActivity;
import com.gat.feature.search.SearchScreen;

/**
 * Created by ducbtsn on 3/25/17.
 */

public class SuggestionActivity extends ScreenActivity<SuggestionScreen, SuggestionPresenter> implements MainTabFragment.OnTabSelectedListener{
    //private MainTabFragment tabFragment;

    @Override
    protected int getLayoutResource() {
        return R.layout.suggestion_activity;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create tabs
        MainTabFragment tabFragment = new MainTabFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(MainTabFragment.POSITION_BUNDLE, MainTabFragment.TAB_POS.TAB_HOME);
        tabFragment.setArguments(bundle);

        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(R.id.tab_fragment, tabFragment);
        transaction.commit();
    }

    @Override
    protected SuggestionScreen getDefaultScreen() {
        return SuggestionScreen.instance();
    }

    @Override
    protected Class<SuggestionPresenter> getPresenterClass() {
        return SuggestionPresenter.class;
    }

    @Override
    public void onTabSelectedListener(int position) {
        switch (position) {
            case MainTabFragment.TAB_POS.TAB_HOME:
                break;
            case MainTabFragment.TAB_POS.TAB_PERSONAL:
                // TODO
                break;
            case MainTabFragment.TAB_POS.TAB_SCAN:
                start(this, ScanBarcodeActivity.class, SearchScreen.instance(Strings.EMPTY));
                break;
            case MainTabFragment.TAB_POS.TAB_NOTIFICATION:
                // TODO
                break;
            case MainTabFragment.TAB_POS.TAB_SETTING:
                // TODO
                break;
            default:
                throw new UnsupportedOperationException();
        }
    }
}
