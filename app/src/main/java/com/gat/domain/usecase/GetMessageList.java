package com.gat.domain.usecase;

import com.gat.repository.MessageRepository;
import com.gat.repository.entity.Message;

import java.util.List;

import io.reactivex.Observable;

/**
 * Created by ducbtsn on 3/30/17.
 */

public class GetMessageList extends UseCase<List<Message>> {
    private final MessageRepository repository;
    private final String userId;
    public GetMessageList(MessageRepository repository, String userId) {
        this.repository = repository;
        this.userId = userId;
    }
    @Override
    protected Observable<List<Message>> createObservable() {
        return repository.getMessageList(userId);
    }
}
