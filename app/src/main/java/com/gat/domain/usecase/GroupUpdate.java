package com.gat.domain.usecase;

import com.gat.repository.MessageRepository;
import com.gat.repository.entity.Group;

import dagger.Lazy;
import io.reactivex.Observable;

/**
 * Created by ducbtsn on 4/23/17.
 */

public class GroupUpdate extends UseCase<Group> {
    private final MessageRepository messageRepository;

    public GroupUpdate(MessageRepository repositoryLazy) {
        this.messageRepository = repositoryLazy;
    }
    @Override
    protected Observable<Group> createObservable() {
        return messageRepository.groupUpdate();
    }
}
