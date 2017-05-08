package com.gat.domain.usecase;

import com.gat.data.response.ServerResponse;
import com.gat.repository.UserRepository;
import com.gat.repository.entity.FirebasePassword;

import io.reactivex.Observable;

/**
 * Created by mryit on 5/8/2017.
 */

public class AddEmailPassword extends UseCase<ServerResponse<FirebasePassword>> {

    private final UserRepository userRepository;
    private final String email;
    private final String password;

    public AddEmailPassword(UserRepository userRepository, String email, String password) {
        this.userRepository = userRepository;
        this.email = email;
        this.password = password;
    }

    @Override
    protected Observable<ServerResponse<FirebasePassword>> createObservable() {
        return userRepository.addEmailPassword(email,password);
    }


}
