package com.gat.domain.usecase;

import com.gat.data.response.ServerResponse;
import com.gat.repository.UserRepository;

import io.reactivex.Observable;

/**
 * Created by mryit on 5/8/2017.
 */

public class LinkSocialAccount extends UseCase<ServerResponse> {

    private final UserRepository userRepository;
    private final String socialID;
    private final String socialName;
    private final int socialType;

    public LinkSocialAccount(UserRepository userRepository, String socialID, String socialName, int socialType) {
        this.userRepository = userRepository;
        this.socialID = socialID;
        this.socialName = socialName;
        this.socialType = socialType;
    }

    @Override
    protected Observable<ServerResponse> createObservable() {
        return userRepository.linkSocialAccount(socialID, socialName, socialType);
    }


}
