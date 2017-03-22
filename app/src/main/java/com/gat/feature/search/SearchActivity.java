package com.gat.feature.search;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.gat.R;
import com.gat.app.activity.ScreenActivity;
import com.gat.common.util.Strings;

/**
 * Created by Rey on 2/13/2017.
 */

public class SearchActivity extends ScreenActivity<SearchScreen, SearchPresenter> {

    private SearchView view;

    @Override
    protected int getLayoutResource() {
        return R.layout.search_layout;
    }

    @Override
    protected Class<SearchPresenter> getPresenterClass() {
        return SearchPresenter.class;
    }

    @Override
    protected SearchScreen getDefaultScreen() {
        return SearchScreen.instance(Strings.EMPTY);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null)
            getPresenter().setKeyword(getScreen().keyword());
        view = new SearchView(findViewById(R.id.search_cl), getPresenter());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        view.onDestroy();
        view = null;
    }

}
