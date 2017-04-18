package com.gat.domain.usecase;

import com.gat.data.response.impl.BookReadingInfo;
import com.gat.repository.BookRepository;

import io.reactivex.Observable;

/**
 * Created by mryit on 4/18/2017.
 */

public class GetReadingStatus extends UseCase<BookReadingInfo> {

    private final BookRepository repository;
    private final int editionId;

    public GetReadingStatus(BookRepository repository, int editionId) {
        this.repository = repository;
        this.editionId = editionId;
    }

    @Override
    protected Observable<BookReadingInfo> createObservable() {
        return repository.getReadingStatus(editionId);
    }
}
