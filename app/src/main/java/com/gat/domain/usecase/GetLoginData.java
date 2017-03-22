package com.gat.domain.usecase;

import com.gat.repository.UserRepository;
import com.gat.repository.entity.LoginData;

import io.reactivex.Observable;

/**
 * Created by ducbtsn on 3/4/17.
 */

public class GetLoginData extends UseCase<LoginData> {
    private final UserRepository repository;

    public GetLoginData(UserRepository repository) {
        this.repository = repository;
    }
    @Override
    protected Observable<LoginData> createObservable() {
        return repository.getLoginData();
    }
}
