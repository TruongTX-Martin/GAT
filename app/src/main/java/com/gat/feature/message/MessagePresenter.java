package com.gat.feature.message;

import com.gat.common.adapter.ItemResult;
import com.gat.common.event.LoadingEvent;
import com.rey.mvp2.Presenter;

import io.reactivex.Observable;

/**
 * Created by ducbtsn on 3/27/17.
 */

public interface MessagePresenter extends Presenter{
    void loadMoreMessageList(String groupId);

    void refreshMessageList(String groupId);

    void refreshGroupList();

    void loadMoreGroupList();

    Observable<ItemResult> hasNewItems();

    Observable<LoadingEvent> loadingEvents();

    Observable<ItemResult> itemsChanged();


}
