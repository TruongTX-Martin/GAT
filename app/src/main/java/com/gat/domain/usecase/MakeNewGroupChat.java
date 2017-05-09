package com.gat.domain.usecase;


import com.gat.repository.MessageRepository;

import io.reactivex.Observable;

/**
 * Created by ducbtsn on 5/9/17.
 */

public class MakeNewGroupChat extends UseCase<Boolean> {
    private final MessageRepository messageRepository;
    private final int userId;

    public MakeNewGroupChat(MessageRepository messageRepository, int userId) {
        this.messageRepository = messageRepository;
        this.userId = userId;
    }

    @Override
    protected Observable<Boolean> createObservable() {
        return messageRepository.makeNewGroup(userId);
    }
}
