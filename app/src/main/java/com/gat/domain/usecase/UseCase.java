package com.gat.domain.usecase;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.internal.functions.Functions;

/**
 * Created by Rey on 3/17/2016.
 */
public abstract class UseCase<T> {

    private Scheduler executeScheduler;
    private Scheduler returnScheduler;
    private Disposable disposable;

    private Consumer<T> nextConsumer;
    private Consumer<Throwable> errorConsumer;
    private Action completeAction;
    private Action stopAction;

    public UseCase<T> executeOn(Scheduler scheduler){
        executeScheduler = scheduler;
        return this;
    }

    public UseCase<T> returnOn(Scheduler scheduler){
        returnScheduler = scheduler;
        return this;
    }

    public UseCase<T> onNext(Consumer<T> nextConsumer){
        this.nextConsumer = nextConsumer;
        return this;
    }

    public UseCase<T> onError(Consumer<Throwable> errorConsumer){
        this.errorConsumer = errorConsumer;
        return this;
    }

    public UseCase<T> onComplete(Action completeAction){
        this.completeAction = completeAction;
        return this;
    }

    /**
     * An Action run on both case onError() or onComplete() is called.
     */
    public UseCase<T> onStop(Action stopAction){
        this.stopAction = stopAction;
        return this;
    }

    public UseCase<T> execute(){
        Observable observable = createObservable();
        if (executeScheduler != null)
            observable = observable.subscribeOn(executeScheduler);
        if (returnScheduler != null)
            observable = observable.observeOn(returnScheduler);
        if(stopAction != null)
            observable = observable.doFinally(stopAction);

        Consumer<T> next = nextConsumer == null ? Functions.emptyConsumer() : nextConsumer;
        Consumer<Throwable> error = errorConsumer == null ? Functions.emptyConsumer() : errorConsumer;
        Action complete = completeAction == null ? Functions.EMPTY_ACTION : completeAction;

        disposable = observable.subscribe(next, error, complete);

        return this;
    }

    public UseCase<T> release() {
        if (disposable != null)
            disposable.dispose();
        disposable = null;
        return this;
    }

    protected abstract Observable<T> createObservable();
}
