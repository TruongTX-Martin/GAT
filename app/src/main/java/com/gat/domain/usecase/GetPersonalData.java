package com.gat.domain.usecase;

import com.gat.repository.UserRepository;
import com.gat.feature.personal.entity.Data;

import io.reactivex.Observable;

/**
 * Created by truongtechno on 23/03/2017.
 */

public class GetPersonalData extends  UseCase<Data> {

    private UserRepository repository;
    public GetPersonalData(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    protected Observable<Data> createObservable() {
        return repository.getPersonalData();
    }
}
