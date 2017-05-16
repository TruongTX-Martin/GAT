package com.gat.feature.bookdetailsender;

import com.gat.data.exception.CommonException;
import com.gat.data.exception.LoginException;
import com.gat.data.response.ResponseData;
import com.gat.data.response.ServerResponse;
import com.gat.domain.SchedulerFactory;
import com.gat.domain.UseCaseFactory;
import com.gat.domain.UseCases;
import com.gat.domain.usecase.UseCase;
import com.gat.feature.bookdetailsender.entity.ChangeStatusResponse;
import com.gat.feature.personal.entity.RequestStatusInput;
import com.gat.repository.entity.Data;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

/**
 * Created by root on 23/04/2017.
 */

public class BookDetailSenderPresenterImpl implements BookDetailSenderPresenter {


    private UseCaseFactory useCaseFactory;
    private SchedulerFactory schedulerFactory;

    private CompositeDisposable bookDetailDisposable;
    private final Subject<Data> bookDetailResultSubject;
    private final Subject<Integer> bookDetailInputSubject;
    private final Subject<ServerResponse<ResponseData>> bookDetailError;
    private UseCase<Data> bookDetailUsecase;

    private CompositeDisposable senderChangeStatusDisposable;
    private final Subject<String> senderChangeStatusResultSubject;
    private final Subject<RequestStatusInput> senderChangeStatusInputSubject;
    private final Subject<String> senderChangeStatusError;
    private UseCase<String> senderChangeStatusUsecase;



    public BookDetailSenderPresenterImpl(UseCaseFactory useCaseFactory, SchedulerFactory factory) {
        this.useCaseFactory = useCaseFactory;
        this.schedulerFactory = factory;

        this.bookDetailError = PublishSubject.create();
        bookDetailResultSubject = PublishSubject.create();
        bookDetailInputSubject = BehaviorSubject.create();


        this.senderChangeStatusError = PublishSubject.create();
        senderChangeStatusResultSubject = PublishSubject.create();
        senderChangeStatusInputSubject = BehaviorSubject.create();
    }

    @Override
    public void onCreate() {
        bookDetailDisposable = new CompositeDisposable(
                bookDetailInputSubject.observeOn(schedulerFactory.main()).subscribe(this::getBookDetail)
        );
        senderChangeStatusDisposable = new CompositeDisposable(
                senderChangeStatusInputSubject.observeOn(schedulerFactory.main()).subscribe(this::getChangeStatus)
        );
    }

    @Override
    public void onDestroy() {
        bookDetailDisposable.dispose();
        senderChangeStatusDisposable.dispose();
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
    public void requestSenderChangeStatus(RequestStatusInput input) {
        senderChangeStatusInputSubject.onNext(input);
    }

    @Override
    public Observable<String> getResponseSenderChangeStatus() {
        return senderChangeStatusResultSubject.observeOn(schedulerFactory.main());
    }

    @Override
    public Observable<String> onErrorSenderChangeStatus() {
        return senderChangeStatusError.observeOn(schedulerFactory.main());
    }

    private void getChangeStatus(RequestStatusInput input){
        senderChangeStatusUsecase = UseCases.release(senderChangeStatusUsecase);
        senderChangeStatusUsecase = useCaseFactory.requestBookByBorrower(input);
        senderChangeStatusUsecase.executeOn(schedulerFactory.io())
                .returnOn(schedulerFactory.main())
                .onNext(response -> {
                    senderChangeStatusResultSubject.onNext(response);
                })
                .onError(throwable -> {
                    if (throwable instanceof CommonException)
                        if (throwable instanceof CommonException)
                            senderChangeStatusError.onNext(((CommonException)throwable).getMessage());
                        else
                            senderChangeStatusError.onNext(ServerResponse.EXCEPTION.message());
                })
                .onStop(
                        () -> senderChangeStatusUsecase = UseCases.release(senderChangeStatusUsecase)
                )
                .execute();
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
                    if (throwable instanceof LoginException)
                        bookDetailError.onNext(ServerResponse.TOKEN_CHANGED);
                    else
                        bookDetailError.onNext(ServerResponse.EXCEPTION);
                })
                .onStop(
                        () -> bookDetailUsecase = UseCases.release(bookDetailUsecase)
                )
                .execute();
    }
}
