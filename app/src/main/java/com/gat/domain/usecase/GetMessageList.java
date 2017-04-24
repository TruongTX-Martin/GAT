package com.gat.domain.usecase;

import com.gat.repository.MessageRepository;
import com.gat.repository.entity.Message;

import java.util.List;

import io.reactivex.Observable;

/**
 * Created by ducbtsn on 3/30/17.
 */

public class GetMessageList extends UseCase<List<Message>> {
    private final MessageRepository repository;
    private final String groupId;
    private final int page;
    private final int size;
    public GetMessageList(MessageRepository repository, String groupId, int page, int size) {
        this.repository = repository;
        this.groupId = groupId;
        this.page = page;
        this.size = size;
    }
    @Override
    protected Observable<List<Message>> createObservable() {
        return repository.getMessageList(groupId, page, size);
    }
}
