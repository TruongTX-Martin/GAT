package com.gat.feature.suggestion;

import com.gat.data.response.ServerResponse;
import com.gat.repository.entity.Book;
import com.rey.mvp2.Presenter;

import java.util.List;

import io.reactivex.Observable;

/**
 * Created by ducbtsn on 3/25/17.
 */

public interface SuggestionPresenter extends Presenter{

    Observable<List<Book>> onResult();

    Observable<String> onError();

    void suggestMostSearched();

}
