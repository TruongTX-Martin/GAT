package com.gat.domain.usecase;

import com.gat.data.response.ServerResponse;
import com.gat.data.response.impl.LoginResponseData;
import com.gat.repository.UserRepository;
import com.gat.repository.entity.LoginData;
import com.gat.repository.entity.User;
import com.google.auto.value.AutoValue;

import io.reactivex.Observable;

/**
 * Created by ducbtsn on 2/26/17.
 */

public class Register extends UseCase<User> {
    private final UserRepository repository;
    private final LoginData data;

    public Register(UserRepository repository, LoginData data){
        this.repository = repository;
        this.data = data;
    }

    @Override
    protected Observable<User> createObservable() {
        return repository.register(data);
    }
}
