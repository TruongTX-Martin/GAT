package com.gat.domain.usecase;

import com.gat.repository.MessageRepository;

import io.reactivex.Observable;

/**
 * Created by ducbtsn on 4/27/17.
 */

public class SawMessage extends UseCase<Boolean> {
    private final MessageRepository repository;
    private final String groupId;
    private final long timeStamp;

    public SawMessage(MessageRepository repository, String groupId, long timeStamp) {
        this.repository = repository;
        this.groupId = groupId;
        this.timeStamp = timeStamp;
    }
    @Override
    protected Observable<Boolean> createObservable() {
        return repository.sawMessage(groupId, timeStamp);
    }
}
