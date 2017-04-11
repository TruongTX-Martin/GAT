package com.gat.feature.suggestion.search;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.gat.R;
import com.gat.data.response.BookResponse;
import com.gat.feature.suggestion.search.listener.OnFragmentRequestLoadMore;
import com.gat.feature.suggestion.search.listener.OnSearchBookResult;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by mryit on 4/9/2017.
 */

public class SearchBookFragment extends Fragment implements OnSearchBookResult{


    @BindView(R.id.buttonLoadMore)
    Button buttonLoadMore;

    private OnFragmentRequestLoadMore requestLoadMore;

    public SearchBookFragment () {}

    @SuppressLint("ValidFragment")
    public SearchBookFragment (OnFragmentRequestLoadMore requestLoadMore) {
        this.requestLoadMore = requestLoadMore;
    }



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_search_book, container, false);

        ButterKnife.bind(this, view);

        return view;
    }


    @OnClick(R.id.buttonLoadMore)
    void onBtnTestLoadMoreTap () {
        this.requestLoadMore.requestLoadMoreBookSearchByTitle();
    }


    @Override
    public void onSearchBookResult(List<BookResponse> list) {

    }
}
