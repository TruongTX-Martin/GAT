package com.gat.domain.usecase;

import com.gat.feature.personal.entity.BookChangeStatusInput;
import com.gat.repository.UserRepository;
import com.gat.repository.entity.Data;
import com.gat.repository.entity.User;

import io.reactivex.Observable;

/**
 * Created by truongtechno on 06/04/2017.
 */

public class GetVisitorInfo extends UseCase<User> {

    private UserRepository userRepository;
    private int userId;
    public GetVisitorInfo(UserRepository userRepository, int input) {
        this.userRepository = userRepository;
        this.userId = input;
    }
    @Override
    protected Observable<User> createObservable() {
        return userRepository.getUserPublicInfo(userId);
    }
}
