package com.gat.domain.usecase;

import com.gat.feature.personal.entity.BookChangeStatusInput;
import com.gat.feature.personal.entity.BookReadingInput;
import com.gat.repository.UserRepository;
import com.gat.repository.entity.Data;

import io.reactivex.Observable;

/**
 * Created by truongtechno on 06/04/2017.
 */

public class GetReadingBooks extends UseCase<Data> {

    private UserRepository userRepository;
    private BookReadingInput input;
    public GetReadingBooks(UserRepository userRepository, BookReadingInput input) {
        this.userRepository = userRepository;
        this.input = input;
    }
    @Override
    protected Observable<Data> createObservable() {
        return userRepository.getReadingBooks(input);
    }
}
