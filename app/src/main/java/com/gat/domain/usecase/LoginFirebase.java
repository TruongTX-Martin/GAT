package com.gat.domain.usecase;

import com.gat.repository.UserRepository;

import io.reactivex.Observable;

/**
 * Created by ducbtsn on 5/1/17.
 */

public class LoginFirebase extends UseCase<Boolean>{
    private final UserRepository userRepository;

    public LoginFirebase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    @Override
    protected Observable<Boolean> createObservable() {
        return userRepository.loginFirebase();
    }
}
