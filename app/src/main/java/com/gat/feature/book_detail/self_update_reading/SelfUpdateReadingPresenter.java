package com.gat.feature.book_detail.self_update_reading;

import com.gat.data.response.impl.BookReadingInfo;
import com.rey.mvp2.Presenter;

import io.reactivex.Observable;

/**
 * Created by mryit on 4/20/2017.
 */

public interface SelfUpdateReadingPresenter extends Presenter{

    void setEditionId (int editionId);
    void setReadingStatus (int readingStatus);

    void updateReadingStatus (int state);
    Observable<String> onUpdateReadingStatusSuccess ();

}
