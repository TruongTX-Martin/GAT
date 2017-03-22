package com.gat.domain.usecase;

import com.gat.repository.UserRepository;
import com.gat.repository.entity.User;

import io.reactivex.Observable;

/**
 * Created by Rey on 2/14/2017.
 */

public class GetUser extends UseCase<User> {

    private final UserRepository repository;

    public GetUser(UserRepository repository){
        this.repository = repository;
    }

    @Override
    protected Observable<User> createObservable() {
        return repository.getUser();
    }
}
