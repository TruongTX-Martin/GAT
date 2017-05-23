package com.gat.feature.book_detail.self_update_reading;

import com.gat.data.response.ResponseData;
import com.gat.data.response.ServerResponse;
import com.gat.data.response.impl.BookReadingInfo;
import com.rey.mvp2.Presenter;

import io.reactivex.Observable;

/**
 * Created by mryit on 4/20/2017.
 */

public interface SelfUpdateReadingPresenter extends Presenter{

    void updateReadingStatus (int editionId, int state, Integer readingId, int bookId);
    Observable<String> onUpdateReadingStatusSuccess ();

    Observable<String> onUnAuthorization();
    Observable<String> onError ();

}
