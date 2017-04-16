package com.gat.domain.usecase;

import com.gat.repository.MessageRepository;
import com.gat.repository.entity.Group;

import java.util.List;

import io.reactivex.Observable;

/**
 * Created by ducbtsn on 4/7/17.
 */

public class LoadMoreGroup extends UseCase<List<Group>> {
    private final MessageRepository messageRepository;

    public LoadMoreGroup(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    @Override
    protected Observable<List<Group>> createObservable() {
        return messageRepository.loadMoreGroup();
    }
}
