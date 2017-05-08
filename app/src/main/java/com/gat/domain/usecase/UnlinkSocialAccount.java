package com.gat.domain.usecase;

import com.gat.data.response.ServerResponse;
import com.gat.repository.UserRepository;
import io.reactivex.Observable;

/**
 * Created by mryit on 5/8/2017.
 */

public class UnlinkSocialAccount extends UseCase<ServerResponse> {

    private final UserRepository userRepository;
    private final int socialType;

    public UnlinkSocialAccount(UserRepository userRepository, int socialType) {
        this.userRepository = userRepository;
        this.socialType = socialType;
    }

    @Override
    protected Observable<ServerResponse> createObservable() {
        return userRepository.unlinkSocialAccount(socialType);
    }

}
