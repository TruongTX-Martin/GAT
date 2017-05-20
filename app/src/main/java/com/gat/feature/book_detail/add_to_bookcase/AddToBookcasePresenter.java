package com.gat.feature.book_detail.add_to_bookcase;

import com.gat.data.response.impl.BookInstanceInfo;
import com.rey.mvp2.Presenter;

import io.reactivex.Observable;

/**
 * Created by mryit on 4/22/2017.
 */

public interface AddToBookcasePresenter extends Presenter {

    void setEditionId (int editionId);

    void getBookTotalInstance ();
    Observable<BookInstanceInfo> onGetBookTotalInstanceSuccess ();

    void addBookInstance (int sharingStatus, int numberOfBook);
    Observable<String> onAddBookInstanceSuccess ();
    Observable<String> onAddBookInstanceFailure ();

    Observable<String> onUnAuthorization ();
}
