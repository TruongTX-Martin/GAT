package com.gat.domain.usecase;

import com.gat.data.response.ServerResponse;
import com.gat.repository.BookRepository;

import io.reactivex.Observable;

/**
 * Created by mryit on 4/18/2017.
 */

public class PostComment extends UseCase<ServerResponse> {

    private final BookRepository repository;
    private final int editionId;
    private final int value;
    private final String review;
    private final boolean spoiler;

    public PostComment(BookRepository repository, int editionId, int value, String review, boolean spoiler) {
        this.repository = repository;
        this.editionId = editionId;
        this.value = value;
        this.review = review;
        this.spoiler = spoiler;
    }

    @Override
    protected Observable<ServerResponse> createObservable() {
        return repository.postComment(editionId, value, review, spoiler);
    }
}
