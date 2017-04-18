package com.gat.domain.usecase;

import com.gat.data.response.impl.EvaluationItemResponse;
import com.gat.repository.BookRepository;

import java.util.List;

import io.reactivex.Observable;

/**
 * Created by mryit on 4/18/2017.
 */

public class GetBookEditionEvaluation extends UseCase<List<EvaluationItemResponse>> {

    private final BookRepository repository;
    private final int editionId;

    public GetBookEditionEvaluation(BookRepository repository, int editionId) {
        this.repository = repository;
        this.editionId = editionId;
    }

    @Override
    protected Observable<List<EvaluationItemResponse>> createObservable() {
        return repository.getBookEditionEvaluation(editionId);
    }
}
