package com.gat.domain.usecase;

import com.gat.data.response.ServerResponse;
import com.gat.repository.UserRepository;

import io.reactivex.Observable;

/**
 * Created by mryit on 5/8/2017.
 */

public class ChangeOldPassword extends UseCase<ServerResponse> {

    private final UserRepository userRepository;
    private final String oldPassword;
    private final String newPassword;

    public ChangeOldPassword(UserRepository userRepository, String oldPassword, String newPassword) {
        this.userRepository = userRepository;
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
    }

    @Override
    protected Observable<ServerResponse> createObservable() {
        return userRepository.changeOldPassword(newPassword, oldPassword);
    }

}
