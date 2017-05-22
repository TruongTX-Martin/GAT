package com.gat.feature.suggestion.search;

import com.gat.data.response.BookResponse;
import com.gat.data.response.DataResultListResponse;
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
    Observable<DataResultListResponse<BookResponse>> onSearchBookWithTitleSuccess();
    Observable<DataResultListResponse<BookResponse>> onLoadMoreBookWithTitleSuccess();
    Observable<Boolean> onCanLoadMoreBookWithTitle ();
    Observable<Integer> onSearchBookWithTitleTotalResult ();


    void loadHistorySearchAuthor();
    Observable<List<Keyword>> onLoadHistorySearchAuthorSuccess();

    void searchBookWithAuthor (String author);
    void loadMoreBookWithAuthor();
    Observable<DataResultListResponse<BookResponse>> onSearchBookWithAuthorSuccess();
    Observable<DataResultListResponse<BookResponse>> onLoadMoreBookWithAuthorSuccess();
    Observable<Boolean> onCanLoadMoreBookWithAuthor ();
    Observable<Integer> onSearchBookWithAuthorTotalResult ();


    void loadHistorySearchUser();
    Observable<List<Keyword>> onLoadHistorySearchUserSuccess();

    void searchUserWithName (String name);
    void loadMoreUserWithName ();
    Observable<DataResultListResponse<UserResponse>> onSearchUserWithNameSuccess();
    Observable<DataResultListResponse<UserResponse>> onLoadMoreUserWithNameSuccess();
    Observable<Boolean> onCanLoadMoreUserWithName ();
    Observable<Integer> onSearchUserWithNameTotalResult ();

    Observable<String> onError ();
    Observable<String> onShowProgressFragment ();
    Observable<String> onHideProgressFragment ();

}