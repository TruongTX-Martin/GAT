package com.gat.domain.usecase;

import com.gat.repository.MessageRepository;
import com.gat.repository.entity.Message;

import java.util.List;

import io.reactivex.Observable;

/**
 * Created by ducbtsn on 4/7/17.
 */

public class LoadMoreMessage extends UseCase<List<Message>> {
    private final MessageRepository messageRepository;

    public LoadMoreMessage(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    @Override
    protected Observable<List<Message>> createObservable() {
        return messageRepository.loadMoreMessage();
    }
}
