package com.gat.feature.suggestion.search;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.gat.R;
import com.gat.common.util.MZDebug;
import com.gat.data.response.BookResponse;
import com.gat.data.response.UserResponse;
import com.gat.feature.suggestion.search.listener.OnFragmentRequestLoadMore;
import com.gat.feature.suggestion.search.listener.OnLoadHistorySuccess;
import com.gat.feature.suggestion.search.listener.OnSearchBookResult;
import com.gat.feature.suggestion.search.listener.OnSearchUserResult;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by mryit on 4/9/2017.
 */

public class SearchResultFragment extends Fragment implements OnSearchBookResult, OnLoadHistorySuccess, OnSearchUserResult {

    @BindView(R.id.buttonLoadMore)
    Button buttonLoadMore;

    @BindView(R.id.recycler_view_search_result)
    RecyclerView recyclerViewSearchResult;

    private OnFragmentRequestLoadMore requestLoadMore;
    private int mTabType = 0;

    public SearchResultFragment() {}

    @SuppressLint("ValidFragment")
    public SearchResultFragment(int tab_position, OnFragmentRequestLoadMore requestLoadMore) {
        this.mTabType = tab_position;
        this.requestLoadMore = requestLoadMore;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_result, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @OnClick(R.id.buttonLoadMore)
    void onBtnTestLoadMoreTap () {
        this.requestLoadMore.requestLoadMoreSearchResult();
    }


    @Override
    public void onLoadHistoryResult(List<String> list) {
        MZDebug.w("onLoadHistoryResult đã nhận được history || TAB = " + mTabType);
    }

    @Override
    public void onSearchBookResult(List<BookResponse> list) {
        if (null == list) {
            MZDebug.w("WARNING: onSearchBookResult : list null _________________________________W");
            return;
        }
        MZDebug.w("WARNING: onSearchBookResult : list size = " + list.size());
    }

    @Override
    public void onSearchUserResult(List<UserResponse> list) {
        if (null == list) {
            MZDebug.w("WARNING: onSearchUserResult : list null _________________________________W");
            return;
        }
        MZDebug.w("WARNING: onSearchUserResult : list size = " + list.size());

    }

}
