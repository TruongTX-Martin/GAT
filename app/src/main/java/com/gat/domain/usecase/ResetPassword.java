package com.gat.domain.usecase;

import com.gat.data.response.ServerResponse;
import com.gat.data.response.impl.LoginResponseData;
import com.gat.repository.UserRepository;

import io.reactivex.Observable;

/**
 * Created by ducbtsn on 3/3/17.
 */

public class ResetPassword extends UseCase<ServerResponse> {
    private String password;
    private final UserRepository repository;

    public ResetPassword(UserRepository repository, String password) {
        this.repository = repository;
        this.password = password;
    }
    @Override
    protected Observable<ServerResponse> createObservable() {
        return repository.changePassword(password);
    }
}
