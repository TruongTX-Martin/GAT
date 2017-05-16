package com.gat.domain.usecase;

import com.gat.feature.bookdetailsender.entity.ChangeStatusResponse;
import com.gat.feature.personal.entity.RequestStatusInput;
import com.gat.repository.UserRepository;
import com.gat.repository.entity.Data;

import io.reactivex.Observable;

/**
 * Created by root on 29/04/2017.
 */

public class RequestBookByBorrower extends UseCase<String> {

    private UserRepository userRepository;
    private RequestStatusInput input;
    public RequestBookByBorrower(UserRepository userRepository, RequestStatusInput input) {
        this.userRepository = userRepository;
        this.input = input;
    }
    @Override
    protected Observable<String> createObservable() {
        return userRepository.requestBookByBorrowrer(input);
    }
}
