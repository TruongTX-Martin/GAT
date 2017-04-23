package com.gat.feature.suggestion.search;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.gat.R;
import com.gat.common.adapter.Item;
import com.gat.common.listener.IRecyclerViewItemClickListener;
import com.gat.common.listener.LoadMoreScrollListener;
import com.gat.common.util.MZDebug;
import com.gat.data.response.BookResponse;
import com.gat.data.response.UserResponse;
import com.gat.feature.personaluser.PersonalUserActivity;
import com.gat.feature.suggestion.search.item.SearchBookResultItem;
import com.gat.feature.suggestion.search.item.SearchBuilder;
import com.gat.feature.suggestion.search.item.SearchHistoryItem;
import com.gat.feature.suggestion.search.item.SearchUserResultItem;
import com.gat.feature.suggestion.search.listener.OnFragmentRequestLoadMore;
import com.gat.feature.suggestion.search.listener.OnUserTapOnKeyword;
import com.gat.feature.suggestion.search.listener.OnLoadHistorySuccess;
import com.gat.feature.suggestion.search.listener.OnSearchBookResult;
import com.gat.feature.suggestion.search.listener.OnSearchUserResult;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mryit on 4/9/2017.
 */

public class SearchResultFragment extends Fragment
        implements OnSearchBookResult, OnLoadHistorySuccess, OnSearchUserResult,
        IRecyclerViewItemClickListener{

    @BindView(R.id.text_view_title)
    TextView textViewTitle;

    @BindView(R.id.recycler_view_search)
    RecyclerView recyclerView;

    private OnUserTapOnKeyword onUserTapOnKeyword;
    private OnFragmentRequestLoadMore requestLoadMore;
    private int mTabType = 0;
    
    private LoadMoreScrollListener loadMoreScrollListener;
    private SearchBookAdapter searchResultAdapter;

    public SearchResultFragment() {}

    @SuppressLint("ValidFragment")
    public SearchResultFragment(int tab_position, OnFragmentRequestLoadMore requestLoadMore, OnUserTapOnKeyword onUserTapOnKeyword) {
        this.mTabType = tab_position;
        this.requestLoadMore = requestLoadMore;
        this.onUserTapOnKeyword = onUserTapOnKeyword;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_result, container, false);
        ButterKnife.bind(this, view);

        this.searchResultAdapter = new SearchBookAdapter(mTabType);
        this.searchResultAdapter.setOnItemClickListener(this);

        this.recyclerView.setHasFixedSize(true);
        this.recyclerView.setLayoutManager(
                new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        this.recyclerView.setNestedScrollingEnabled(false);
        this.recyclerView.setAdapter(searchResultAdapter);

        loadMoreScrollListener = new LoadMoreScrollListener(3, true, () -> {
            // request load more
            MZDebug.e("____________________________________________________ REQUEST LOAD MORE");
        });
        recyclerView.addOnScrollListener(loadMoreScrollListener);

        return view;
    }

    @Override
    public void onLoadHistoryResult(List<String> list) {
        MZDebug.w("onLoadHistoryResult đã nhận được history || TAB = " + mTabType + ", list size = " + list.size());

        List<Item> listItems = SearchBuilder.transformListHistory(list);
        this.searchResultAdapter.setItem(listItems);
    }

    @Override
    public void onSearchBookResult(List<BookResponse> list) {
        if (null == list) {
            MZDebug.w("WARNING: onSearchBookResult : list null _________________________________W");
            return;
        }

        this.textViewTitle.setText("Hiển thị " + list.size() + " kết quả tìm kiếm");

        this.searchResultAdapter.clearAllItems();
        List<Item> listItems = SearchBuilder.transformListBook(list);
        this.searchResultAdapter.setItem(listItems);
    }

    @Override
    public void onSearchUserResult(List<UserResponse> list) {
        if (null == list) {
            MZDebug.w("WARNING: onSearchUserResult : list null _________________________________W");
            return;
        }

        // set title
        this.textViewTitle.setText("Hiển thị " + list.size() + " kết quả tìm kiếm");

        // remove all items
        this.searchResultAdapter.clearAllItems();

        // set new list items result
        List<Item> listItems = SearchBuilder.transformListUser(list);
        this.searchResultAdapter.setItem(listItems);
    }

    @Override
    public void onItemClickListener(View v, int position) {
        Item item = searchResultAdapter.getItemAt(position);

        if (item instanceof SearchHistoryItem) {
            SearchHistoryItem historyItem = (SearchHistoryItem) item;
            onUserTapOnKeyword.onUserTapOnHistoryKeyword(historyItem.keyword());
        } else if (item instanceof SearchBookResultItem) {
            SearchBookResultItem bookItem = (SearchBookResultItem) item;
            Toast.makeText(getActivity(), "Book id: " + bookItem.bookResponse().getBookId() + ", EditionId: " + bookItem.bookResponse().getEditionId(), Toast.LENGTH_SHORT).show();
        } else if (item instanceof SearchUserResultItem) {
            SearchUserResultItem userItem = (SearchUserResultItem) item;
//            Toast.makeText(getActivity(), "User id: " + userItem.userResponse().getUserId(), Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(SuggestSearchActivity.instance, PersonalUserActivity.class);
            intent.putExtra("UserInfo",userItem.userResponse());
            SuggestSearchActivity.instance.startActivity(intent);
        }
    }
}
