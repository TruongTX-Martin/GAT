package com.gat.domain.usecase;

import com.gat.data.response.impl.EvaluationItemResponse;
import com.gat.repository.BookRepository;

import io.reactivex.Observable;

/**
 * Created by mryit on 4/18/2017.
 */

public class GetBookEvaluationByUser extends UseCase<EvaluationItemResponse> {

    private final BookRepository repository;
    private final int editionId;

    public GetBookEvaluationByUser(BookRepository repository, int editionId) {
        this.repository = repository;
        this.editionId = editionId;
    }

    @Override
    protected Observable<EvaluationItemResponse> createObservable() {
        return repository.getBookEvaluationByUser(editionId);
    }
}
