package com.gat.domain.usecase;

import com.gat.data.response.ServerResponse;
import com.gat.repository.BookRepository;
import io.reactivex.Observable;

/**
 * Created by mozaa on 21/04/2017.
 */

public class SelfUpdateReadingStatus extends UseCase<ServerResponse> {


    private final BookRepository repository;
    private final int editionId;
    private final int readingStatus;
    private final Integer readingId;
    private final int bookId;

    public SelfUpdateReadingStatus(BookRepository repository, int editionId, int readingStatus, Integer readingId, int bookId) {
        this.repository = repository;
        this.editionId = editionId;
        this.readingStatus = readingStatus;
        this.readingId = readingId;
        this.bookId = bookId;
    }

    @Override
    protected Observable<ServerResponse> createObservable() {
        return repository.selfUpdateReadingStatus(editionId, readingStatus, readingId, bookId);
    }


}
