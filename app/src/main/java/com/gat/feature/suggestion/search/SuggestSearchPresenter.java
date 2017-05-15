package com.gat.feature.suggestion.search;

import com.gat.data.response.BookResponse;
import com.gat.data.response.UserResponse;
import com.gat.data.response.impl.Keyword;
import com.rey.mvp2.Presenter;
import java.util.List;
import io.reactivex.Observable;

/**
 * Created by mryit on 4/9/2017.
 */

public interface SuggestSearchPresenter extends Presenter {

    void loadHistorySearchBook();
    Observable<List<Keyword>> onLoadHistorySearchBookSuccess();

    void searchBookWithTitle (String book_title);
    void loadMoreBookWithTitle();
    Observable<List<BookResponse>> onSearchBookWithTitleSuccess();
    Observable<List<BookResponse>> onLoadMoreBookWithTitleSuccess();
    Observable<Boolean> onCanLoadMoreBookWithTitle ();


    void loadHistorySearchAuthor();
    Observable<List<Keyword>> onLoadHistorySearchAuthorSuccess();

    void searchBookWithAuthor (String author);
    void loadMoreBookWithAuthor();
    Observable<List<BookResponse>> onSearchBookWithAuthorSuccess();
    Observable<List<BookResponse>> onLoadMoreBookWithAuthorSuccess();
    Observable<Boolean> onCanLoadMoreBookWithAuthor ();


    void loadHistorySearchUser();
    Observable<List<Keyword>> onLoadHistorySearchUserSuccess();

    void searchUserWithName (String name);
    void loadMoreUserWithName ();
    Observable<List<UserResponse>> onSearchUserWithNameSuccess();
    Observable<List<UserResponse>> onLoadMoreUserWithNameSuccess();
    Observable<Boolean> onCanLoadMoreUserWithName ();

    Observable<String> onError();

}