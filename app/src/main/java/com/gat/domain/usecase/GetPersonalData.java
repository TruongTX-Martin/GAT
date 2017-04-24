package com.gat.domain.usecase;

import com.gat.repository.UserRepository;
import com.gat.repository.entity.Data;
import com.gat.repository.entity.User;

import io.reactivex.Observable;

/**
 * Created by truongtechno on 23/03/2017.
 */

public class GetPersonalData extends  UseCase<Data<User>> {

    private UserRepository repository;
    public GetPersonalData(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    protected Observable<Data<User>> createObservable() {
        return repository.getPersonalData();
    }
}
