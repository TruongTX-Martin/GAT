package com.gat.domain.usecase;

import com.gat.feature.personal.entity.BookChangeStatusInput;
import com.gat.feature.personaluser.entity.BorrowRequestInput;
import com.gat.repository.UserRepository;
import com.gat.repository.entity.Data;

import io.reactivex.Observable;

/**
 * Created by truongtechno on 06/04/2017.
 */

public class RequestBorrowBook extends UseCase<Data> {

    private UserRepository userRepository;
    private BorrowRequestInput input;
    public RequestBorrowBook(UserRepository userRepository, BorrowRequestInput input) {
        this.userRepository = userRepository;
        this.input = input;
    }
    @Override
    protected Observable<Data> createObservable() {
        return userRepository.requestBorrowBook(input);
    }
}
