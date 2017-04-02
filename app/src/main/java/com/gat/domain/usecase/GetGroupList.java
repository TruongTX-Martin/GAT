package com.gat.domain.usecase;

import com.gat.repository.MessageRepository;
import com.gat.repository.entity.Group;

import java.util.List;

import io.reactivex.Observable;

/**
 * Created by ducbtsn on 4/1/17.
 */

public class GetGroupList extends UseCase<List<Group>> {
    private final MessageRepository messageRepository;

    public GetGroupList(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }
    @Override
    protected Observable<List<Group>> createObservable() {
        return messageRepository.getGroupList();
    }
}
