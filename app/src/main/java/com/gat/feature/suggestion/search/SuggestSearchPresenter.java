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

    void searchBookWithTitle (String book_title);
    void loadMoreBookWithTitle(String book_title);
    Observable<List<BookResponse>> onSearchBookWithTitleSuccess ();


    void searchBookWithAuthor (String author);
    void loadMoreBookWithAuthor(String author);
    Observable<List<BookResponse>> onSearchBookWithAuthorSuccess ();


    void searchUserWithName (String name);
    void loadMoreUserWithName (String name);
    Observable<List<UserResponse>> onSearchUserWithNameSuccess ();


}




