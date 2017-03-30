package com.gat.repository;

import com.gat.repository.entity.Message;

import java.util.List;

import io.reactivex.Observable;

/**
 * Created by ducbtsn on 3/30/17.
 */

public interface MessageRepository {
    public Observable<List<Message>> getMessageList();
}
