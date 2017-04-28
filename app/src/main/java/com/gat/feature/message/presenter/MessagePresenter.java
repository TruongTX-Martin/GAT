package com.gat.feature.message.presenter;

import com.gat.common.adapter.ItemResult;
import com.gat.common.event.LoadingEvent;
import com.gat.repository.entity.User;
import com.rey.mvp2.Presenter;

import io.reactivex.Observable;

/**
 * Created by ducbtsn on 3/27/17.
 */

public interface MessagePresenter extends Presenter{
    void loadMoreMessageList(int userId);

    void refreshMessageList(int userId);

    Observable<LoadingEvent> loadingEvents();

    Observable<ItemResult> itemsChanged();

    void sendMessage(String message);

    void sawMessage(String groupId, long timeStamp);

    Observable<Boolean> sendMessageResult();

    Observable<User> loadUser();
}
