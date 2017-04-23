package com.gat.domain.usecase;


import io.reactivex.Observable;
import io.reactivex.ObservableTransformer;
import io.reactivex.Scheduler;

/**
 * Created by Rey on 11/18/2016.
 */

public class TransformUseCase<T, R> extends UseCase<R> {

    private UseCase<T> baseUseCase;
    private ObservableTransformer<T, R> transformer;
    private Scheduler transformScheduler;

    public TransformUseCase(UseCase<T> baseUseCase, ObservableTransformer<T, R> transformer){
        this.baseUseCase = baseUseCase;
        this.transformer = transformer;
    }

    public TransformUseCase<T, R> transformOn(Scheduler scheduler){
        transformScheduler = scheduler;
        return this;
    }

    @Override
    protected Observable<R> createObservable() {
        Observable<T> observable = baseUseCase.createObservable();
        if(transformScheduler != null)
            observable = observable.observeOn(transformScheduler);
        return observable.compose(transformer);
    }
}
