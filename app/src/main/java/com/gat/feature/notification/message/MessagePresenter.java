package com.gat.feature.notification.message;

import com.gat.common.adapter.ItemResult;
import com.gat.common.event.LoadingEvent;
import com.rey.mvp2.Presenter;

import io.reactivex.Observable;

/**
 * Created by ducbtsn on 3/27/17.
 */

public interface MessagePresenter extends Presenter{
    void loadMoreMessageList();

    void refreshMessageList();

    Observable<LoadingEvent> loadingEvents();

    Observable<ItemResult> itemsChanged();
}
