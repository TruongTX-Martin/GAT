package com.gat.feature.bookdetail;

import com.gat.data.response.ResponseData;
import com.gat.data.response.ServerResponse;
import com.gat.domain.SchedulerFactory;
import com.gat.domain.UseCaseFactory;
import com.gat.domain.UseCases;
import com.gat.domain.usecase.UseCase;
import com.gat.repository.entity.Data;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

/**
 * Created by root on 23/04/2017.
 */

public class BookDetailRequestPresenterImpl implements BookDetailRequestPresenter {


    private UseCaseFactory useCaseFactory;
    private SchedulerFactory schedulerFactory;

    private CompositeDisposable bookDetailDisposable;
    private final Subject<Data> bookDetailResultSubject;
    private final Subject<Integer> bookDetailInputSubject;
    private final Subject<ServerResponse<ResponseData>> bookDetailError;
    private UseCase<Data> bookDetailUsecase;


    public BookDetailRequestPresenterImpl(UseCaseFactory useCaseFactory, SchedulerFactory factory) {
        this.useCaseFactory = useCaseFactory;
        this.schedulerFactory = factory;

        this.bookDetailError = PublishSubject.create();
        bookDetailResultSubject = PublishSubject.create();
        bookDetailInputSubject = BehaviorSubject.create();
    }

    @Override
    public void onCreate() {
        bookDetailDisposable = new CompositeDisposable(
                bookDetailInputSubject.observeOn(schedulerFactory.main()).subscribe(this::getBookDetail)
        );
    }

    @Override
    public void onDestroy() {
        bookDetailDisposable.dispose();
    }

    @Override
    public void requestBookDetail(int input) {
        bookDetailInputSubject.onNext(input);
    }

    @Override
    public Observable<Data> getResponseBookDetail() {
        return bookDetailResultSubject.observeOn(schedulerFactory.main());
    }

    @Override
    public Observable<ServerResponse<ResponseData>> onErrorBookDetail() {
        return bookDetailError.observeOn(schedulerFactory.main());
    }


    private void getBookDetail(Integer input){
        bookDetailUsecase = UseCases.release(bookDetailUsecase);
        bookDetailUsecase = useCaseFactory.getBookDetail(input);
        bookDetailUsecase.executeOn(schedulerFactory.io())
                .returnOn(schedulerFactory.main())
                .onNext(response -> {
                    bookDetailResultSubject.onNext(response);
                })
                .onError(throwable -> {
                    bookDetailError.onError(throwable);
                })
                .onStop(
                        () -> bookDetailUsecase = UseCases.release(bookDetailUsecase)
                )
                .execute();
    }
}
