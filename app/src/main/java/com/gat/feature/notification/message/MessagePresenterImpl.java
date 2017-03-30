package com.gat.feature.notification.message;

import com.gat.common.adapter.ItemResult;
import com.gat.common.event.LoadingEvent;

import io.reactivex.Observable;

/**
 * Created by ducbtsn on 3/27/17.
 */

public class MessagePresenterImpl implements MessagePresenter {
    @Override
    public void onCreate() {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public void loadMoreMessageList() {

    }

    @Override
    public void refreshMessageList() {

    }

    @Override
    public Observable<LoadingEvent> loadingEvents() {
        return null;
    }

    @Override
    public Observable<ItemResult> itemsChanged() {
        return null;
    }
}
