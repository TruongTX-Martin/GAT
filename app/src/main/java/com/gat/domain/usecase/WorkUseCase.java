package com.gat.domain.usecase;

import java.util.concurrent.Callable;

import io.reactivex.Observable;

/**
 * Created by Rey on 2/15/2017.
 */

public class WorkUseCase<T> extends UseCase<T> {

    private final Callable<T> callable;

    public WorkUseCase(Callable<T> callable){
        this.callable = callable;
    }

    @Override
    protected Observable<T> createObservable() {
        return Observable.fromCallable(callable);
    }
}
