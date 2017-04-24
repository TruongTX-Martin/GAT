package com.gat.feature.scanbarcode;

import com.gat.data.exception.CommonException;
import com.gat.domain.SchedulerFactory;
import com.gat.domain.UseCaseFactory;
import com.gat.domain.UseCases;
import com.gat.domain.usecase.UseCase;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.Subject;

/**
 * Created by ducbtsn on 4/24/17.
 */

public class ScanPresenterImpl implements ScanPresenter {
    private final UseCaseFactory useCaseFactory;
    private final SchedulerFactory schedulerFactory;

    private final Subject<String> isbnSubject;
    private final Subject<Integer> resultSubject;
    private final Subject<String> errorSubject;

    private UseCase<Integer> getByIsbnUseCase;
    private CompositeDisposable compositeDisposable;

    public ScanPresenterImpl(UseCaseFactory useCaseFactory, SchedulerFactory schedulerFactory) {
        this.useCaseFactory = useCaseFactory;
        this.schedulerFactory = schedulerFactory;

        isbnSubject = BehaviorSubject.create();
        resultSubject = BehaviorSubject.create();
        errorSubject = BehaviorSubject.create();
    }

    @Override
    public void searchByIsbn(String isbn) {
        isbnSubject.onNext(isbn);
    }

    private void searchBookByIsbn(String isbn) {
        getByIsbnUseCase = UseCases.release(getByIsbnUseCase);
        getByIsbnUseCase = useCaseFactory.getBookByIsbn(isbn);

        getByIsbnUseCase.executeOn(schedulerFactory.io())
                .returnOn(schedulerFactory.main())
                .onNext(response -> {
                    resultSubject.onNext(response);
                })
                .onError(throwable -> {
                    if (throwable instanceof CommonException)
                        errorSubject.onNext(((CommonException)throwable).getMessage());
                    else {
                        throwable.printStackTrace();
                        errorSubject.onNext("Exception occurred.");
                    }
                })
                .execute();
    }

    @Override
    public Observable<Integer> onSuccess() {
        return resultSubject.observeOn(schedulerFactory.main());
    }

    @Override
    public Observable<String> onError() {
        return errorSubject.observeOn(schedulerFactory.main());
    }


    @Override
    public void onCreate() {
        compositeDisposable = new CompositeDisposable(
                isbnSubject.observeOn(schedulerFactory.main()).subscribe(this::searchBookByIsbn)
        );
    }

    @Override
    public void onDestroy() {
        getByIsbnUseCase = UseCases.release(getByIsbnUseCase);
        compositeDisposable.dispose();
    }
}
