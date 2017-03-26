package com.gat.common.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gat.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by ducbtsn on 3/25/17.
 */

public class MainTabFragment extends Fragment{
    public final static String POSITION_BUNDLE = "position";

    @Retention(RetentionPolicy.SOURCE)
    public @interface TAB_POS {
        int TAB_HOME            = 0;
        int TAB_PERSONAL        = 1;
        int TAB_SCAN            = 2;
        int TAB_NOTIFICATION    = 3;
        int TAB_SETTING         = 4;
    }
    private TabLayout tabLayout;

    private TabLayout.OnTabSelectedListener tabSelectedListener;

    private int selectedPosition;

    private OnTabSelectedListener myTabSelectedListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof OnTabSelectedListener) {
            myTabSelectedListener = (OnTabSelectedListener)context;
        }
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_layout, container, false);

        if (savedInstanceState != null) {
            selectedPosition = savedInstanceState.getInt(POSITION_BUNDLE);
        } else {
            selectedPosition = 0;
        }

        //Initializing the tablayout
        tabLayout = (TabLayout) view.findViewById(R.id.tabLayout);

        //Adding the tabs using addTab() method
        tabLayout.addTab(tabLayout.newTab().setText(R.string.tab_home));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.tab_personal));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.tab_scanbarcode));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.tab_notification));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.tab_setting));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        TabLayout.Tab tabCall = tabLayout.getTabAt(TAB_POS.TAB_HOME);
        tabCall.setIcon(R.drawable.home_ic);
        tabCall = tabLayout.getTabAt(TAB_POS.TAB_PERSONAL);
        tabCall.setIcon(R.drawable.personal_ic);
        tabCall = tabLayout.getTabAt(TAB_POS.TAB_SCAN);
        tabCall.setIcon(R.drawable.scan_ic);
        tabCall = tabLayout.getTabAt(TAB_POS.TAB_NOTIFICATION);
        tabCall.setIcon(R.drawable.notic_ic);
        tabCall = tabLayout.getTabAt(TAB_POS.TAB_SETTING);
        tabCall.setIcon(R.drawable.setting_ic);

        tabSelectedListener = new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                selectTab(tab, true);
                selectedPosition = tab.getPosition();
                if (myTabSelectedListener != null) {
                    myTabSelectedListener.onTabSelectedListener(selectedPosition);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                selectTab(tab, false);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        };

        tabLayout.addOnTabSelectedListener(tabSelectedListener);

        tabLayout.getTabAt(selectedPosition).select();

        return view;

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        tabLayout.clearOnTabSelectedListeners();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(POSITION_BUNDLE, selectedPosition);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
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

    public interface OnTabSelectedListener {
        public void onTabSelectedListener(int position);
    }
}
