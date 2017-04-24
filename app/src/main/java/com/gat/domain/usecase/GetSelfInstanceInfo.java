package com.gat.domain.usecase;

import com.gat.data.response.impl.BookInstanceInfo;
import com.gat.repository.BookRepository;

import io.reactivex.Observable;

/**
 * Created by mryit on 4/18/2017.
 */

public class GetSelfInstanceInfo extends UseCase<BookInstanceInfo> {

    private final BookRepository repository;
    private final int editionId;

    public GetSelfInstanceInfo(BookRepository repository, int editionId) {
        this.repository = repository;
        this.editionId = editionId;
    }

    @Override
    protected Observable<BookInstanceInfo> createObservable() {
        return repository.getSelfInstanceInfo(editionId);
    }
}
