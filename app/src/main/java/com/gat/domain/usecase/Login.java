package com.gat.domain.usecase;

import com.gat.repository.UserRepository;
import com.gat.repository.entity.LoginData;
import com.gat.repository.entity.User;

import io.reactivex.Observable;

/**
 * Created by Rey on 2/14/2017.
 */

public class Login extends UseCase<User> {

    private final UserRepository repository;
    private final LoginData data;

    public Login(UserRepository repository, LoginData data){
        this.repository = repository;
        this.data = data;
    }

    @Override
    protected Observable<User> createObservable() {
        if (this.data == LoginData.EMPTY)
            return repository.login();
        return repository.login(data);
    }
}
