package com.gat.domain.usecase;

import com.gat.repository.UserRepository;

import java.util.List;

import io.reactivex.Observable;

/**
 * Created by mozaa on 11/04/2017.
 */

public class GetUsersSearchedKeyword extends UseCase<List<String>> {

    private final UserRepository userRepository;

    public GetUsersSearchedKeyword(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    protected Observable<List<String>> createObservable() {
        return userRepository.getUsersSearchedKeyword();
    }

}
