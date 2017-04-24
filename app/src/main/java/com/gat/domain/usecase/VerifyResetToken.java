package com.gat.domain.usecase;

import com.gat.data.response.ResponseData;
import com.gat.data.response.ServerResponse;
import com.gat.data.response.impl.VerifyTokenResponseData;
import com.gat.repository.UserRepository;

import io.reactivex.Observable;

/**
 * Created by ducbtsn on 3/3/17.
 */

public class VerifyResetToken extends UseCase<ServerResponse> {
    private String code;
    private final UserRepository repository;

    public VerifyResetToken(UserRepository repository, String code) {
        this.repository = repository;
        this.code = code;
    }

    @Override
    protected Observable<ServerResponse> createObservable() {
        return repository.verifyToken(code);
    }
}
