package com.gat.domain.usecase;

import com.gat.data.response.impl.Keyword;
import com.gat.repository.UserRepository;

import java.util.List;

import io.reactivex.Observable;

/**
 * Created by mozaa on 11/04/2017.
 */

public class GetUsersSearchedKeyword extends UseCase<List<Keyword>> {

    private final UserRepository userRepository;

    public GetUsersSearchedKeyword(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    protected Observable<List<Keyword>> createObservable() {
        return userRepository.getUsersSearchedKeyword();
    }

}
