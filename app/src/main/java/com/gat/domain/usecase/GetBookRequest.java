package com.gat.domain.usecase;

import com.gat.feature.personal.entity.BookRequestInput;
import com.gat.repository.UserRepository;
import com.gat.repository.entity.Data;

import io.reactivex.Observable;

/**
 * Created by truongtechno on 30/03/2017.
 */

public class GetBookRequest extends UseCase<Data> {

    private UserRepository userRepository;
    private BookRequestInput input;
    public GetBookRequest(UserRepository userRepository, BookRequestInput input) {
        this.userRepository = userRepository;
        this.input = input;
    }

    @Override
    protected Observable<Data> createObservable() {
        return userRepository.getBookRequest(input);
    }
}
