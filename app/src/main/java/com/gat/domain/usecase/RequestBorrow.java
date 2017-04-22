package com.gat.domain.usecase;

import com.gat.data.response.impl.BorrowResponse;
import com.gat.repository.BookRepository;

import io.reactivex.Observable;

/**
 * Created by mryit on 4/22/2017.
 */

public class RequestBorrow extends UseCase<BorrowResponse> {

    private final BookRepository repository;
    private final int editionId;
    private final int ownerId;

    public RequestBorrow(BookRepository repository, int editionId, int ownerId) {
        this.repository = repository;
        this.editionId = editionId;
        this.ownerId = ownerId;
    }

    @Override
    protected Observable<BorrowResponse> createObservable() {
        return null;
    }


}
