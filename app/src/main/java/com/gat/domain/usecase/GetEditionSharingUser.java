package com.gat.domain.usecase;

import com.gat.data.response.UserResponse;
import com.gat.repository.BookRepository;

import java.util.List;

import io.reactivex.Observable;

/**
 * Created by mryit on 4/18/2017.
 */

public class GetEditionSharingUser extends UseCase<List<UserResponse>> {

    private final BookRepository repository;
    private final int editionId;

    public GetEditionSharingUser(BookRepository repository, int editionId) {
        this.repository = repository;
        this.editionId = editionId;
    }

    @Override
    protected Observable<List<UserResponse>> createObservable() {
        return repository.getEditionSharingUser(editionId);
    }
}
