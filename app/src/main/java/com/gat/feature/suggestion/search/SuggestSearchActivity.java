package com.gat.feature.suggestion.search;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import com.gat.R;
import com.gat.app.activity.ScreenActivity;
import com.gat.common.adapter.ViewPagerAdapter;
import com.gat.common.util.MZDebug;
import com.gat.common.util.Strings;
import com.gat.data.response.BookResponse;
import com.gat.data.response.UserResponse;
import com.gat.feature.search.SearchActivity;
import com.gat.feature.search.SearchScreen;
import com.gat.feature.suggestion.search.listener.OnFragmentRequestLoadMore;
import com.gat.feature.suggestion.search.listener.OnLoadHistorySuccess;
import com.gat.feature.suggestion.search.listener.OnSearchBookResult;
import com.gat.feature.suggestion.search.listener.OnSearchUserResult;
import com.gat.feature.suggestion.search.listener.OnUserTapOnKeyword;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.disposables.CompositeDisposable;
import android.support.design.widget.TabLayout.OnTabSelectedListener;

/**
 * Created by mryit on 4/9/2017.
 */

public class SuggestSearchActivity extends ScreenActivity<SuggestSearchScreen, SuggestSearchPresenter>
        implements OnFragmentRequestLoadMore, EditText.OnEditorActionListener,
        OnUserTapOnKeyword{

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
    private CompositeDisposable disposables;
    private OnLoadHistorySuccess onSearchBookHistorySuccess;
    private OnLoadHistorySuccess onSearchAuthorHistorySuccess;
    private OnLoadHistorySuccess onSearchUserHistorySuccess;
    private OnSearchBookResult onSearchBookResult;
    private OnSearchBookResult onSearchAuthorResult;
    private OnSearchUserResult onSearchUserResult;
    public static SuggestSearchActivity instance;

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        disposables = new CompositeDisposable(
                getPresenter().onLoadHistorySearchBookSuccess().subscribe(this::onLoadHistorySearchBookSuccess),
                getPresenter().onLoadHistorySearchAuthorSuccess().subscribe(this::onLoadHistorySearchAuthorSuccess),
                getPresenter().onLoadHistorySearchUserSuccess().subscribe(this::onLoadHistorySearchUserSuccess),
                getPresenter().onSearchBookWithTitleSuccess().subscribe(this::onSearchBookWithTitleSuccess),
                getPresenter().onSearchBookWithAuthorSuccess().subscribe(this::onSearchBookWithAuthorSuccess),
                getPresenter().onSearchUserWithNameSuccess().subscribe(this::onSearchUserWithNameSuccess)
        );

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading..");

        setupViewPager(mViewPager);
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.addOnTabSelectedListener(onTabSelectedListener);

        // listen whenever user tap on search button
        editTextSearch.setOnEditorActionListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // request list history search book by title
        getPresenter().loadHistorySearchBook();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        SearchResultFragment searchResultFragment = new SearchResultFragment(TAB_POS.TAB_BOOK, this, this);
        adapter.addFragment(searchResultFragment, getResources().getString(R.string.tab_book_name));
        onSearchBookHistorySuccess = searchResultFragment;
        onSearchBookResult = searchResultFragment;

        SearchResultFragment searchAuthorFragment = new SearchResultFragment(TAB_POS.TAB_AUTHOR, this, this);
        adapter.addFragment(searchAuthorFragment,  getResources().getString(R.string.tab_book_author));
        onSearchAuthorHistorySuccess = searchAuthorFragment;
        onSearchAuthorResult = searchAuthorFragment;

        SearchResultFragment searchUserFragment = new SearchResultFragment(TAB_POS.TAB_USER, this, this);
        adapter.addFragment(searchUserFragment,  getResources().getString(R.string.tab_user_name));
        onSearchUserHistorySuccess = searchUserFragment;
        onSearchUserResult = searchUserFragment;

        viewPager.setOffscreenPageLimit(2);
        viewPager.setAdapter(adapter);
    }

    @OnClick(R.id.image_button_search_scan)
    void onButtonSearchScanTap () {
        start(getApplicationContext(), SearchActivity.class, SearchScreen.instance("he"));
    }

    @OnClick(R.id.button_cancel)
    void onButtonCancelTap () {
        finish();
        overridePendingTransition( R.anim.no_change, R.anim.push_down );
    }

    OnTabSelectedListener onTabSelectedListener = new OnTabSelectedListener() {
        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            mCurrentTab = tab.getPosition();

            switch (mCurrentTab) {
                case TAB_POS.TAB_BOOK:
                    MZDebug.w("_______________________________ Request list history BOOK");
                    getPresenter().loadHistorySearchBook();
                    break;
                case TAB_POS.TAB_AUTHOR:
                    MZDebug.w("________________________________ Request list history AUTHOR");
                    getPresenter().loadHistorySearchAuthor();
                    break;
                case TAB_POS.TAB_USER:
                    MZDebug.w("_______________________________ Request list history USER");
                    getPresenter().loadHistorySearchUser();
                    break;
            }
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {

        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {

        }
    };

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId != EditorInfo.IME_ACTION_SEARCH) {
            MZDebug.w("ACTION IS NOT IME_ACTION_SEARCH. ");
            return true;
        }

        switch (mCurrentTab) {
            case TAB_POS.TAB_BOOK:
                MZDebug.w("_____________________ TAB_BOOK: " + editTextSearch.getText().toString());
                getPresenter().searchBookWithTitle(editTextSearch.getText().toString());
                break;

            case TAB_POS.TAB_AUTHOR:
                MZDebug.w("___________________ TAB_AUTHOR: " + editTextSearch.getText().toString());
                getPresenter().searchBookWithAuthor(editTextSearch.getText().toString());
                break;

            case TAB_POS.TAB_USER:
                MZDebug.w("_____________________ TAB_USER: " + editTextSearch.getText().toString());
                getPresenter().searchUserWithName(editTextSearch.getText().toString());
                break;
        }

        return true;
    }

    @Override
    public void onUserTapOnHistoryKeyword(String keyword) {
        editTextSearch.setText(keyword);
    }

    @Override
    public void requestLoadMoreSearchResult() {
        switch (mCurrentTab) {
            case TAB_POS.TAB_BOOK:
                getPresenter().loadMoreBookWithTitle();
                break;

            case TAB_POS.TAB_AUTHOR:
                getPresenter().loadMoreBookWithAuthor();
                break;

            case TAB_POS.TAB_USER:
                getPresenter().loadMoreUserWithName();
                break;
        }
    }

    private void onLoadHistorySearchBookSuccess (List<String> list) {
        onSearchBookHistorySuccess.onLoadHistoryResult(list);
    }

    private void onLoadHistorySearchAuthorSuccess (List<String> list) {
        onSearchAuthorHistorySuccess.onLoadHistoryResult(list);
    }

    private void onLoadHistorySearchUserSuccess (List<String> list) {
        onSearchUserHistorySuccess.onLoadHistoryResult(list);
    }

    private void onSearchBookWithTitleSuccess (List<BookResponse> list) {
        onSearchBookResult.onSearchBookResult(list);
    }

    private void onSearchBookWithAuthorSuccess (List<BookResponse> list) {
        onSearchAuthorResult.onSearchBookResult(list);
    }

    private void onSearchUserWithNameSuccess (List<UserResponse> list) {
        onSearchUserResult.onSearchUserResult(list);
    }

}
