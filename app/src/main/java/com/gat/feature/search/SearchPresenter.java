package com.gat.feature.search;

import com.gat.common.adapter.ItemResult;
import com.gat.common.event.LoadingEvent;
import com.gat.data.response.ServerResponse;
import com.gat.repository.entity.Book;
import com.rey.mvp2.Presenter;

import io.reactivex.Observable;

/**
 * Created by Rey on 2/14/2017.
 */

public interface SearchPresenter extends Presenter {

    Observable<String> keywordChanged();

    Observable<LoadingEvent> loadingEvents();

    Observable<ItemResult> itemsChanged();

    void setKeyword(String keyword);

    void loadMoreBooks();

    void refreshBooks();

    void setIsbn(String isbn);
    Observable<Book> getBookResult();
    Observable<ServerResponse> onError();

}
