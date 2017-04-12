package com.gat.feature.suggestion.search;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import com.gat.R;
import com.gat.app.activity.ScreenActivity;
import com.gat.common.adapter.ViewPagerAdapter;
import com.gat.common.util.MZDebug;
import com.gat.feature.suggestion.search.listener.OnFragmentRequestLoadMore;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by mryit on 4/9/2017.
 */

public class SuggestSearchActivity extends ScreenActivity<SuggestSearchScreen, SuggestSearchPresenter>
        implements OnFragmentRequestLoadMore{

    @BindView(R.id.tab_layout)
    TabLayout mTabLayout;

    @BindView(R.id.view_pager)
    ViewPager mViewPager;

    @BindView(R.id.edit_text_search)
    EditText editTextSearch;


    @Retention(RetentionPolicy.SOURCE)
    public @interface TAB_POS {
        int TAB_BOOK    = 0;
        int TAB_AUTHOR  = 1;
        int TAB_USER    = 2;
    }

    private int mCurrentTab = 0;
    private ProgressDialog progressDialog;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_search;
    }

    @Override
    protected Class<SuggestSearchPresenter> getPresenterClass() {
        return SuggestSearchPresenter.class;
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

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading..");

        setupViewPager(mViewPager);
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mCurrentTab = tab.getPosition();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        editTextSearch.setOnEditorActionListener((view, actionId, event) -> {
            if (actionId != EditorInfo.IME_ACTION_UNSPECIFIED) {
                MZDebug.w("ACTION IS NOT IME_ACTION_SEARCH. ");
                return true;
            }

            switch (mCurrentTab) {
                case TAB_POS.TAB_AUTHOR:
                    MZDebug.w("TAB_AUTHOR: " + editTextSearch.getText().toString());
                    break;

                case TAB_POS.TAB_BOOK:
                    MZDebug.w("TAB_BOOK: " + editTextSearch.getText().toString());
                    break;

                case TAB_POS.TAB_USER:
                    MZDebug.w("TAB_USER: " + editTextSearch.getText().toString());
                    break;
            }

            return true;
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new SearchBookFragment(this), "Tên sách");
        adapter.addFragment(new SearchAuthorFragment(), "Tác giả");
        adapter.addFragment(new SearchUserFragment(), "Người dùng");
        viewPager.setAdapter(adapter);
    }

    @Override
    public void requestLoadMoreBookSearchByTitle() {
        MZDebug.e("requestLoadMoreBookSearchByTitle TAP");
    }

    @Override
    public void requestLoadMoreBookSearchByAuthor() {

    }

    @Override
    public void requestLoadMoreUserSearchByName() {

    }
}
