package com.gat.domain.usecase;

import com.gat.feature.personal.entity.BookRequestInput;
import com.gat.feature.personaluser.entity.BookSharingUserInput;
import com.gat.repository.UserRepository;
import com.gat.repository.entity.Data;

import io.reactivex.Observable;

/**
 * Created by truongtechno on 30/03/2017.
 */

public class GetBookUserSharing extends UseCase<Data> {

    private UserRepository userRepository;
    private BookSharingUserInput input;
    public GetBookUserSharing(UserRepository userRepository, BookSharingUserInput input) {
        this.userRepository = userRepository;
        this.input = input;
    }

    @Override
    protected Observable<Data> createObservable() {
        return userRepository.getBookUserSharing(input);
    }
}
