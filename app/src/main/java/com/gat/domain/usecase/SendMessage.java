package com.gat.domain.usecase;

import com.gat.repository.MessageRepository;

import io.reactivex.Observable;

/**
 * Created by ducbtsn on 4/8/17.
 */

public class SendMessage extends UseCase<Boolean> {
    private final String message;
    private final String toUserId;
    private final MessageRepository repository;

    public SendMessage(MessageRepository messageRepository, String message, String toUserId) {
        this.repository = messageRepository;
        this.message = message;
        this.toUserId = toUserId;
    }
    @Override
    protected Observable<Boolean> createObservable() {
        return repository.sendMessage(toUserId, message);
    }
}
