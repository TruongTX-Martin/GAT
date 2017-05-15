package com.gat.feature.suggestion.search;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import com.gat.R;
import com.gat.app.activity.ScreenActivity;
import com.gat.common.adapter.ViewPagerAdapter;
import com.gat.common.event.LoadingEvent;
import com.gat.common.util.ClientUtils;
import com.gat.common.util.MZDebug;
import com.gat.common.util.Strings;
import com.gat.common.util.Views;
import com.gat.data.response.BookResponse;
import com.gat.data.response.UserResponse;
import com.gat.data.response.impl.Keyword;
import com.gat.feature.search.SearchActivity;
import com.gat.feature.search.SearchScreen;
import com.gat.feature.suggestion.search.listener.OnFragmentRequestLoadMore;
import com.gat.feature.suggestion.search.listener.OnLoadHistorySuccess;
import com.gat.feature.suggestion.search.listener.OnSearchBookResult;
import com.gat.feature.suggestion.search.listener.OnSearchCanLoadMore;
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
import android.widget.Toast;

/**
 * Created by mryit on 4/9/2017.
 */

public class SuggestSearchActivity extends ScreenActivity<SuggestSearchScreen, SuggestSearchPresenter>
        implements OnFragmentRequestLoadMore, EditText.OnEditorActionListener, EditText.OnTouchListener,
        OnUserTapOnKeyword{

    @BindView(R.id.tab_layout)
    TabLayout mTabLayout;

    @BindView(R.id.view_pager)
    ViewPager mViewPager;

    @BindView(R.id.edit_text_search)
    EditText editTextSearch;

    @BindView(R.id.image_button_search_scan)
    ImageButton imageButtonScan;

    @Retention(RetentionPolicy.SOURCE)
    public @interface BUTTON_TYPE {
        int SCAN    = 0;
        int CLEAR   = 1;
    }

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
    private OnSearchCanLoadMore onSearchBookCanLoadMore;
    private OnSearchCanLoadMore onSearchAuthorCanLoadMore;
    private OnSearchCanLoadMore onSearchUserCanLoadMore;
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
                getPresenter().onSearchUserWithNameSuccess().subscribe(this::onSearchUserWithNameSuccess),
                getPresenter().onError().subscribe(this::onSearchFailure),
                getPresenter().onCanLoadMoreBookWithTitle().subscribe(this::onCanLoadMoreBookWithTitle),
                getPresenter().onCanLoadMoreBookWithAuthor().subscribe(this::onCanLoadMoreBookWithAuthor),
                getPresenter().onCanLoadMoreUserWithName().subscribe(this::onCanLoadMoreUserWithName),
                getPresenter().onLoadMoreBookWithTitleSuccess().subscribe(this::onLoadMoreBookWithTitleSuccess),
                getPresenter().onLoadMoreBookWithAuthorSuccess().subscribe(this::onLoadMoreBookWithAuthorSuccess),
                getPresenter().onLoadMoreUserWithNameSuccess().subscribe(this::onLoadMoreUserWithNameSuccess)
        );

        progressDialog = ClientUtils.createProgressDialog(SuggestSearchActivity.this);

        setupViewPager(mViewPager);
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.addOnTabSelectedListener(onTabSelectedListener);

        // set tag button scan: default = BUTTON_TYPE.SCAN
        imageButtonScan.setTag(BUTTON_TYPE.SCAN);

        // listen whenever user tap on search button
        editTextSearch.setOnEditorActionListener(this);
        editTextSearch.setOnTouchListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // request list history search book by title
        getPresenter().loadHistorySearchBook();
    }

    @Override
    protected void onDestroy() {
        disposables.dispose();
        hideProgress();
        progressDialog = null;
        super.onDestroy();
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        SearchResultFragment searchResultFragment = new SearchResultFragment(TAB_POS.TAB_BOOK, this, this);
        adapter.addFragment(searchResultFragment, getResources().getString(R.string.tab_book_name));
        onSearchBookHistorySuccess = searchResultFragment;
        onSearchBookResult = searchResultFragment;
        onSearchBookCanLoadMore = searchResultFragment;

        SearchResultFragment searchAuthorFragment = new SearchResultFragment(TAB_POS.TAB_AUTHOR, this, this);
        adapter.addFragment(searchAuthorFragment,  getResources().getString(R.string.tab_book_author));
        onSearchAuthorHistorySuccess = searchAuthorFragment;
        onSearchAuthorResult = searchAuthorFragment;
        onSearchAuthorCanLoadMore = searchAuthorFragment;

        SearchResultFragment searchUserFragment = new SearchResultFragment(TAB_POS.TAB_USER, this, this);
        adapter.addFragment(searchUserFragment,  getResources().getString(R.string.tab_user_name));
        onSearchUserHistorySuccess = searchUserFragment;
        onSearchUserResult = searchUserFragment;
        onSearchUserCanLoadMore = searchUserFragment;

        viewPager.setOffscreenPageLimit(2);
        viewPager.setAdapter(adapter);
    }

    @OnClick(R.id.image_button_search_scan)
    void onButtonSearchScanTap () {
        int type = (int) imageButtonScan.getTag();

        switch (type) {
            case BUTTON_TYPE.SCAN:
                // start activity scan

                break;

            case BUTTON_TYPE.CLEAR:
                if (TextUtils.isEmpty(editTextSearch.getText().toString())) {
                    imageButtonScan.setTag(BUTTON_TYPE.SCAN);
                    imageButtonScan.setImageDrawable(ResourcesCompat.getDrawable(getResources(),R.drawable.scan_ic_selected, null));
                    Views.hideKeyboard(SuggestSearchActivity.this);
                } else {
                    editTextSearch.setText(Strings.EMPTY);
                }
                break;
        }

    }

    @OnClick(R.id.button_cancel)
    void onButtonCancelTap () {
        finish();
        overridePendingTransition( R.anim.no_change, R.anim.push_down );
    }

    OnTabSelectedListener onTabSelectedListener = new OnTabSelectedListener() {
        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            Views.hideKeyboard(SuggestSearchActivity.this);

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

        showProgress();
        startSearchWithKeyword(editTextSearch.getText().toString());

        return true;
    }

    @Override
    public void onUserTapOnHistoryKeyword(String keyword) {
        editTextSearch.setText(keyword);
        imageButtonScan.setTag(BUTTON_TYPE.CLEAR);
        imageButtonScan.setImageDrawable(ResourcesCompat.getDrawable(getResources(),R.drawable.ic_cancle, null));
        showProgress();
        startSearchWithKeyword(keyword);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        if (MotionEvent.ACTION_UP != event.getAction()) {
            return false;
        }

        if (TextUtils.isEmpty(editTextSearch.getText().toString())) {
            imageButtonScan.setTag(BUTTON_TYPE.CLEAR);
            imageButtonScan.setImageDrawable(ResourcesCompat.getDrawable(getResources(),R.drawable.ic_cancle, null));
        }

        return false;
    }


    private void startSearchWithKeyword (String keyword) {
        switch (mCurrentTab) {
            case TAB_POS.TAB_BOOK:
                MZDebug.w("_____________________ TAB_BOOK: " + keyword);
                getPresenter().searchBookWithTitle(keyword);
                break;

            case TAB_POS.TAB_AUTHOR:
                MZDebug.w("___________________ TAB_AUTHOR: " + keyword);
                getPresenter().searchBookWithAuthor(keyword);
                break;

            case TAB_POS.TAB_USER:
                MZDebug.w("_____________________ TAB_USER: " + keyword);
                getPresenter().searchUserWithName(keyword);
                break;
        }
    }

    @Override
    public void requestLoadMoreSearchResult() {
        showProgress();
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

    private void onLoadHistorySearchBookSuccess (List<Keyword> list) {
        hideProgress();
        onSearchBookHistorySuccess.onLoadHistoryResult(list);
    }

    private void onLoadHistorySearchAuthorSuccess (List<Keyword> list) {
        hideProgress();
        onSearchAuthorHistorySuccess.onLoadHistoryResult(list);
    }

    private void onLoadHistorySearchUserSuccess (List<Keyword> list) {
        onSearchUserHistorySuccess.onLoadHistoryResult(list);
        hideProgress();
    }

    private void onSearchBookWithTitleSuccess (List<BookResponse> list) {
        hideProgress();
        onSearchBookResult.onSearchBookResult(list);
    }

    private void onSearchBookWithAuthorSuccess (List<BookResponse> list) {
        hideProgress();
        onSearchAuthorResult.onSearchBookResult(list);
    }

    private void onSearchUserWithNameSuccess (List<UserResponse> list) {
        hideProgress();
        onSearchUserResult.onSearchUserResult(list);
    }

    private void onSearchFailure (String message) {
        hideProgress();
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void onCanLoadMoreBookWithTitle (Boolean isCanLoadMore) {
        onSearchBookCanLoadMore.onCanLoadMoreItem(TAB_POS.TAB_BOOK);
    }

    private void onCanLoadMoreBookWithAuthor (Boolean isCanLoadMore) {
        onSearchAuthorCanLoadMore.onCanLoadMoreItem(TAB_POS.TAB_AUTHOR);
    }

    private void onCanLoadMoreUserWithName (Boolean isCanLoadMore) {
        onSearchUserCanLoadMore.onCanLoadMoreItem(TAB_POS.TAB_USER);
    }


    private void onLoadMoreBookWithTitleSuccess (List<BookResponse> list) {
        hideProgress();
        onSearchBookCanLoadMore.onLoadMoreBookWithTitleSuccess(list);
    }

    private void onLoadMoreBookWithAuthorSuccess (List<BookResponse> list) {
        hideProgress();
        onSearchAuthorCanLoadMore.onLoadMoreBookWithAuthorSuccess(list);
    }

    private void onLoadMoreUserWithNameSuccess (List<UserResponse> list) {
        hideProgress();
        onSearchUserCanLoadMore.onLoadMoreUserSuccess(list);
    }

    private void showProgress () {
        Views.hideKeyboard(this);
        progressDialog.show();
    }

    private void hideProgress () {
        if (progressDialog.isShowing()) {
            progressDialog.hide();
        }
    }
}
