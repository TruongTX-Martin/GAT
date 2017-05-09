package com.gat.domain.usecase;

import com.gat.repository.UserRepository;

import io.reactivex.Observable;

/**
 * Created by mryit on 5/10/2017.
 */

public class SignOut extends UseCase<Boolean>{

    private final UserRepository repository;

    public SignOut(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    protected Observable<Boolean> createObservable() {
        return repository.signOut();
    }
}
