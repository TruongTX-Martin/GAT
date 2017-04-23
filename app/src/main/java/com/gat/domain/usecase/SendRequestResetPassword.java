package com.gat.domain.usecase;

import com.gat.data.response.ServerResponse;
import com.gat.data.response.impl.ResetPasswordResponseData;
import com.gat.repository.UserRepository;

import io.reactivex.Observable;

/**
 * Created by ducbtsn on 3/2/17.
 */

public class SendRequestResetPassword extends UseCase<ServerResponse> {
    private String email;
    private final UserRepository repository;
    public SendRequestResetPassword(UserRepository repository, String email) {
        this.repository = repository;
        this.email = email;
    }
    @Override
    protected Observable<ServerResponse> createObservable() {
        return this.repository.sendRequestResetPassword(this.email);
    }
}
