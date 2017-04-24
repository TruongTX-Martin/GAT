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
    private final int page;
    private final int size;

    public GetGroupList(MessageRepository messageRepository, int page, int size) {
        this.messageRepository = messageRepository;
        this.page = page;
        this.size = size;
    }
    @Override
    protected Observable<List<Group>> createObservable() {
        return messageRepository.getGroupList(page, size);
    }
}
