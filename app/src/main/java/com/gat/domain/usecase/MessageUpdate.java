package com.gat.domain.usecase;

import com.gat.repository.MessageRepository;
import com.gat.repository.entity.Message;

import io.reactivex.Observable;

/**
 * Created by ducbtsn on 5/2/17.
 */

public class MessageUpdate extends UseCase<Message> {
    private final int userId;
    private final MessageRepository messageRepository;

    public MessageUpdate(int userId, MessageRepository messageRepository) {
        this.userId = userId;
        this.messageRepository = messageRepository;
    }

    @Override
    protected Observable<Message> createObservable() {
        return messageRepository.messageUpdate(userId);
    }
}
