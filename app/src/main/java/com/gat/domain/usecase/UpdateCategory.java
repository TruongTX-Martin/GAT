package com.gat.domain.usecase;

import com.gat.data.response.ServerResponse;
import com.gat.repository.UserRepository;

import java.util.List;

import io.reactivex.Observable;

/**
 * Created by ducbtsn on 3/5/17.
 */

public class UpdateCategory extends UseCase<ServerResponse> {
    private final UserRepository repository;
    private final List<Integer> categories;

    public UpdateCategory(UserRepository repository, List<Integer> categories) {
        this.repository = repository;
        this.categories = categories;
    }
    @Override
    protected Observable<ServerResponse> createObservable() {
        return repository.updateCategories(categories);
    }
}
