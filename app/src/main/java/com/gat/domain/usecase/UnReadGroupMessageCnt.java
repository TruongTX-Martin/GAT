package com.gat.domain.usecase;

import com.gat.repository.MessageRepository;

import io.reactivex.Observable;

/**
 * Created by ducbtsn on 5/2/17.
 */

public class UnReadGroupMessageCnt extends UseCase<Integer> {
    private final MessageRepository repository;

    public UnReadGroupMessageCnt(MessageRepository messageRepository) {
        this.repository = messageRepository;
    }
    @Override
    protected Observable<Integer> createObservable() {
        return repository.getGroupUnReadCnt();
    }
}
