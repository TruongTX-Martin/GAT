package com.gat.feature.message.presenter;

import com.gat.common.adapter.ItemResult;
import com.gat.common.event.LoadingEvent;
import com.rey.mvp2.Presenter;

import io.reactivex.Observable;

/**
 * Created by ducbtsn on 4/24/17.
 */

public interface GroupMessagePresenter extends Presenter{
    void refreshGroupList();

    void loadMoreGroupList();

    void update();

    Observable<LoadingEvent> loadingEvents();

    Observable<ItemResult> itemsChanged();
}
