package com.gat.domain.usecase;

import com.gat.feature.personal.entity.BookChangeStatusInput;
import com.gat.feature.personal.entity.BookInstanceInput;
import com.gat.repository.UserRepository;
import com.gat.repository.entity.Data;

import io.reactivex.Observable;

/**
 * Created by truongtechno on 06/04/2017.
 */

public class ChangeBookSharingStatus extends UseCase<Data> {

    private UserRepository userRepository;
    private BookChangeStatusInput input;
    public ChangeBookSharingStatus(UserRepository userRepository, BookChangeStatusInput input) {
        this.userRepository = userRepository;
        this.input = input;
    }
    @Override
    protected Observable<Data> createObservable() {
        return userRepository.changeBookSharingStatus(input);
    }
}
