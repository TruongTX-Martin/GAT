package com.gat.domain.usecase;

import com.gat.data.response.impl.BookInfo;
import com.gat.repository.BookRepository;
import com.gat.repository.UserRepository;

import io.reactivex.Observable;

/**
 * Created by mryit on 4/18/2017.
 */

public class RemoveBook extends UseCase<String> {

    private final UserRepository repository;
    private final int instanceId;

    public RemoveBook(UserRepository repository, int editionId) {
        this.repository = repository;
        this.instanceId = editionId;
    }

    @Override
    protected Observable<String> createObservable() {
        return repository.removeBook(this.instanceId);
    }

}
