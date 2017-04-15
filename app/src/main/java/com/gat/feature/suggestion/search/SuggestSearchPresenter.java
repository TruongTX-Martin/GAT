package com.gat.feature.suggestion.search;

import com.gat.data.response.BookResponse;
import com.gat.data.response.UserResponse;
import com.rey.mvp2.Presenter;
import java.util.List;
import io.reactivex.Observable;

/**
 * Created by mryit on 4/9/2017.
 */

public interface SuggestSearchPresenter extends Presenter {

    void loadHistorySearchBook();
    Observable<List<String>> onLoadHistorySearchBookSuccess();

    void searchBookWithTitle (String book_title);
    void loadMoreBookWithTitle();
    Observable<List<BookResponse>> onSearchBookWithTitleSuccess();


    void loadHistorySearchAuthor();
    Observable<List<String>> onLoadHistorySearchAuthorSuccess();

    void searchBookWithAuthor (String author);
    void loadMoreBookWithAuthor();
    Observable<List<BookResponse>> onSearchBookWithAuthorSuccess();


    void loadHistorySearchUser();
    Observable<List<String>> onLoadHistorySearchUserSuccess();

    void searchUserWithName (String name);
    void loadMoreUserWithName ();
    Observable<List<UserResponse>> onSearchUserWithNameSuccess();

    Observable<String> onError();

}