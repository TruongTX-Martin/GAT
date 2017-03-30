package com.gat.repository.impl;

import com.gat.repository.MessageRepository;
import com.gat.repository.datasource.MessageDataSource;
import com.gat.repository.entity.Message;

import java.util.List;

import dagger.Lazy;
import io.reactivex.Observable;

/**
 * Created by ducbtsn on 3/30/17.
 */

public class MesssageRepositoryImpl implements MessageRepository {
    private final Lazy<MessageDataSource> networkDataSource;
    private final Lazy<MessageDataSource> localDataSource;

    public MesssageRepositoryImpl(Lazy<MessageDataSource> networkDataSource, Lazy<MessageDataSource> localDataSource) {
        this.networkDataSource = networkDataSource;
        this.localDataSource = localDataSource;
    }
    @Override
    public Observable<List<Message>> getMessageList() {
        return Observable.defer(() -> networkDataSource.get().getMessageList());
    }
}
