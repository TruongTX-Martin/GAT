package com.gat.domain.usecase;

import com.gat.data.response.impl.BookInfo;
import com.gat.repository.BookRepository;

import io.reactivex.Observable;

/**
 * Created by mryit on 4/18/2017.
 */

public class GetBookInfo extends UseCase<BookInfo> {

    private final BookRepository repository;
    private final int editionId;

    public GetBookInfo(BookRepository repository, int editionId) {
        this.repository = repository;
        this.editionId = editionId;
    }

    @Override
    protected Observable<BookInfo> createObservable() {
        return repository.getBookInfo(this.editionId);
    }

}
