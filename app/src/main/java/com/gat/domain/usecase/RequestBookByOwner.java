package com.gat.domain.usecase;

import com.gat.feature.bookdetailsender.entity.ChangeStatusResponse;
import com.gat.feature.personal.entity.RequestStatusInput;
import com.gat.repository.UserRepository;

import io.reactivex.Observable;

/**
 * Created by root on 29/04/2017.
 */

public class RequestBookByOwner extends UseCase<ChangeStatusResponse> {

    private UserRepository userRepository;
    private RequestStatusInput input;
    public RequestBookByOwner(UserRepository userRepository, RequestStatusInput input) {
        this.userRepository = userRepository;
        this.input = input;
    }
    @Override
    protected Observable<ChangeStatusResponse> createObservable() {
        return userRepository.requestBookByOwner(input);
    }
}
