package com.gat.domain.usecase;

import com.gat.feature.personal.entity.BookInstanceInput;
import com.gat.feature.personal.entity.Data;
import com.gat.repository.UserRepository;

import io.reactivex.Observable;

/**
 * Created by truongtechno on 30/03/2017.
 */

public class GetBookInstance extends UseCase<Data> {

    private UserRepository userRepository;
    private BookInstanceInput input;
    public GetBookInstance(UserRepository userRepository, BookInstanceInput input) {
        this.userRepository = userRepository;
        this.input = input;
    }

    @Override
    protected Observable<Data> createObservable() {
        return userRepository.getBookInstance(input);
    }
}
