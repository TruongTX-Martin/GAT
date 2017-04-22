package com.gat.domain.usecase;

import com.gat.data.response.ServerResponse;
import com.gat.repository.BookRepository;
import io.reactivex.Observable;

/**
 * Created by mryit on 4/18/2017.
 */

public class SelfAddInstance extends UseCase<ServerResponse> {

    private final BookRepository repository;
    private final int editionId;
    private final int sharingStatus;
    private final int numberOfBook;

    public SelfAddInstance(BookRepository repository, int editionId, int sharingStatus, int numberOfBook) {
        this.repository = repository;
        this.editionId = editionId;
        this.sharingStatus = sharingStatus;
        this.numberOfBook = numberOfBook;
    }

    @Override
    protected Observable<ServerResponse> createObservable() {
        return repository.selfAddInstance(editionId, sharingStatus, numberOfBook);
    }
}
