package com.gat.feature.bookdetailowner;

import com.gat.data.response.ResponseData;
import com.gat.data.response.ServerResponse;
import com.gat.domain.SchedulerFactory;
import com.gat.domain.UseCaseFactory;
import com.gat.domain.UseCases;
import com.gat.domain.usecase.UseCase;
import com.gat.feature.personal.entity.RequestStatusInput;
import com.gat.repository.entity.Data;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

/**
 * Created by root on 26/04/2017.
 */

public class BookDetailOwnerPresenterImpl implements BookDetailOwnerPresenter {


    private UseCaseFactory useCaseFactory;
    private SchedulerFactory schedulerFactory;


    private CompositeDisposable bookDetailDisposable;
    private final Subject<Data> bookDetailResultSubject;
    private final Subject<Integer> bookDetailInputSubject;
    private final Subject<ServerResponse<ResponseData>> bookDetailError;
    private UseCase<Data> bookDetailUsecase;


    private CompositeDisposable changeStatusDisposable;
    private final Subject<Data> changeStatusResultSubject;
    private final Subject<RequestStatusInput> changeStatusInputSubject;
    private final Subject<ServerResponse<ResponseData>> changeStatusError;
    private UseCase<Data> changeStatusUsecase;


    public BookDetailOwnerPresenterImpl(UseCaseFactory useCaseFactory, SchedulerFactory factory) {
        this.useCaseFactory = useCaseFactory;
        this.schedulerFactory = factory;


        this.bookDetailError = PublishSubject.create();
        bookDetailResultSubject = PublishSubject.create();
        bookDetailInputSubject = BehaviorSubject.create();


        this.changeStatusError = PublishSubject.create();
        changeStatusResultSubject = PublishSubject.create();
        changeStatusInputSubject = BehaviorSubject.create();
    }

    @Override
    public void onCreate() {
        bookDetailDisposable = new CompositeDisposable(
                bookDetailInputSubject.observeOn(schedulerFactory.main()).subscribe(this::getBookDetail)
        );
        changeStatusDisposable = new CompositeDisposable(
                changeStatusInputSubject.observeOn(schedulerFactory.main()).subscribe(this::getChangeStatus)
        );
    }

    @Override
    public void onDestroy() {
        bookDetailDisposable.dispose();
        changeStatusDisposable.dispose();
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

    @Override
    public void requestChangeStatus(RequestStatusInput input) {
        changeStatusInputSubject.onNext(input);
    }

    @Override
    public Observable<Data> getResponseChangeStatus() {
        return changeStatusResultSubject.observeOn(schedulerFactory.main());
    }

    @Override
    public Observable<ServerResponse<ResponseData>> onErrorChangeStatus() {
        return changeStatusError.observeOn(schedulerFactory.main());
    }

    private void getChangeStatus(RequestStatusInput input){
        changeStatusUsecase = UseCases.release(changeStatusUsecase);
        changeStatusUsecase = useCaseFactory.requestBookByBorrower(input);
        changeStatusUsecase.executeOn(schedulerFactory.io())
                .returnOn(schedulerFactory.main())
                .onNext(response -> {
                    changeStatusResultSubject.onNext(response);
                })
                .onError(throwable -> {
                    changeStatusError.onError(throwable);
                })
                .onStop(
                        () -> changeStatusUsecase = UseCases.release(changeStatusUsecase)
                )
                .execute();
    }
    private void getBookDetail(Integer input) {
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
