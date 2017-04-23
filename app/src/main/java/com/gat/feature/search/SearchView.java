package com.gat.feature.search;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

import com.gat.R;
import com.gat.common.adapter.ItemResult;
import com.gat.common.event.LoadingEvent;
import com.gat.common.listener.LoadMoreScrollListener;
import com.gat.common.util.Strings;
import com.gat.common.util.Views;
import com.jakewharton.rxbinding.widget.RxTextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
//import hu.akarnokd.rxjava.interop.RxJavaInterop;
//import hu.akarnokd.rxjava.interop.RxJavaInterop;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by Rey on 2/22/2017.
 */

public class SearchView {

    private SearchPresenter presenter;
    private View view;
    private Unbinder unbinder;

    @BindView(R.id.search_srl)
    SwipeRefreshLayout refreshLayout;

    @BindView(R.id.search_rv)
    RecyclerView recyclerView;

    @BindView(R.id.search_et_keyword)
    EditText keywordView;

    private SearchAdapter adapter;
    private LoadMoreScrollListener loadMoreScrollListener;

    private CompositeDisposable disposables;

    SearchView(View view, SearchPresenter presenter){
        this.view = view;
        this.presenter = presenter;
        unbinder = ButterKnife.bind(this, view);
        init();
    }

    private void init(){
        adapter = new SearchAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext(), LinearLayoutManager.VERTICAL, false));

        loadMoreScrollListener = new LoadMoreScrollListener(3, true, () -> {
            if(adapter.hasLoadMoreItem())
                presenter.loadMoreBooks();
        });
        recyclerView.addOnScrollListener(loadMoreScrollListener);

        refreshLayout.setColorSchemeResources(R.color.colorAccent);
        refreshLayout.setProgressBackgroundColorSchemeResource(R.color.backgroundCard);
        refreshLayout.setOnRefreshListener(() -> presenter.refreshBooks());

        disposables = new CompositeDisposable(
                presenter.keywordChanged().subscribe(this::onKeywordChanged),
                presenter.itemsChanged().subscribe(this::onItemChanged),
                presenter.loadingEvents().subscribe(this::onLoadingEvent)

//                RxJavaInterop.toV2Observable(RxTextView.editorActionEvents(keywordView))
//                        .filter(event -> event.actionId() == EditorInfo.IME_ACTION_SEARCH)
//                        .doOnNext(event -> Views.hideKeyboard(view.getContext()))
//                        .map(event -> event.view().getText().toString().trim())
//                        .filter(keyword -> !Strings.isNullOrEmpty(keyword))
//                        .subscribe(keyword -> presenter.setKeyword(keyword))
        );
    }

    void onDestroy(){
        view = null;
        presenter = null;
        recyclerView.clearOnScrollListeners();
        recyclerView.setAdapter(null);
        recyclerView.getRecycledViewPool().clear();
        adapter = null;
        loadMoreScrollListener = null;
        disposables.dispose();
        unbinder.unbind();
    }

    private void onKeywordChanged(String keyword){
        keywordView.setText(keyword);
        keywordView.setSelection(keyword.length());
    }

    private void onItemChanged(ItemResult result) {
        if (adapter.setItem(result.items()) && result.diffResult() != null)
            result.diffResult().dispatchUpdatesTo(adapter);
    }

    private void onLoadingEvent(LoadingEvent event) {
        switch (event.status()) {
            case LoadingEvent.Status.BEGIN:
                loadMoreScrollListener.setEnable(false);
                refreshLayout.setRefreshing(false);
                refreshLayout.setEnabled(!event.refresh() && adapter.getItemCount() > 1);
                break;
            case LoadingEvent.Status.DONE:
                loadMoreScrollListener.setEnable(adapter.hasLoadMoreItem());
                refreshLayout.setRefreshing(false);
                refreshLayout.setEnabled(true);
                if (event.refresh())
                    recyclerView.scrollToPosition(0);
                break;
            case LoadingEvent.Status.ERROR:
                loadMoreScrollListener.setEnable(false);
                refreshLayout.setRefreshing(false);
                refreshLayout.setEnabled(true);
                break;
        }
    }
}
