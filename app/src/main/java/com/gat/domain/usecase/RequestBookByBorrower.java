package com.gat.domain.usecase;

import com.gat.feature.personal.entity.RequestStatusInput;
import com.gat.repository.UserRepository;
import com.gat.repository.entity.Data;

import io.reactivex.Observable;

/**
 * Created by root on 29/04/2017.
 */

public class RequestBookByBorrower extends UseCase<Data> {

    private UserRepository userRepository;
    private RequestStatusInput input;
    public RequestBookByBorrower(UserRepository userRepository, RequestStatusInput input) {
        this.userRepository = userRepository;
        this.input = input;
    }
    @Override
    protected Observable<Data> createObservable() {
        return userRepository.requestBookByBorrowrer(input);
    }
}
