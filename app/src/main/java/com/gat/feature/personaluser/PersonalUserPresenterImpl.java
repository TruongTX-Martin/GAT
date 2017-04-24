package com.gat.feature.personaluser;

import com.gat.data.response.ResponseData;
import com.gat.data.response.ServerResponse;
import com.gat.domain.SchedulerFactory;
import com.gat.domain.UseCaseFactory;
import com.gat.domain.UseCases;
import com.gat.domain.usecase.UseCase;
import com.gat.feature.personal.entity.BookReadingInput;
import com.gat.feature.personaluser.entity.BookSharingUserInput;
import com.gat.repository.entity.Data;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

/**
 * Created by root on 20/04/2017.
 */

public class PersonalUserPresenterImpl implements PersonalUserPresenter {


    private UseCaseFactory useCaseFactory;
    private SchedulerFactory schedulerFactory;



    //change book sharing
    private CompositeDisposable bookUserSharingDisposable;
    private UseCase<Data> bookUserSharingUsecase;
    private Subject<Data> bookUserSharingResultSubject;
    private Subject<BookSharingUserInput> bookUserSharingInputSubject;
    private Subject<ServerResponse<ResponseData>> bookUserSharingError;

    //change book reading
    private CompositeDisposable bookUserReadingDisposable;
    private UseCase<Data> bookUserReadingUsecase;
    private Subject<Data> bookUserReadingResultSubject;
    private Subject<BookReadingInput> bookUserReadingInputSubject;
    private Subject<ServerResponse<ResponseData>> bookUserReadingError;

    public PersonalUserPresenterImpl(UseCaseFactory useCaseFactory, SchedulerFactory factory) {
        this.useCaseFactory = useCaseFactory;
        this.schedulerFactory = factory;


        this.bookUserSharingError = PublishSubject.create();
        bookUserSharingResultSubject = PublishSubject.create();
        bookUserSharingInputSubject = BehaviorSubject.create();

        this.bookUserReadingError = PublishSubject.create();
        bookUserReadingResultSubject = PublishSubject.create();
        bookUserReadingInputSubject = BehaviorSubject.create();


    }

    @Override
    public void onCreate() {
        bookUserSharingDisposable = new CompositeDisposable(
                bookUserSharingInputSubject.observeOn(schedulerFactory.main()).subscribe(this::getBookUserSharing)
        );

        bookUserReadingDisposable = new CompositeDisposable(
                bookUserReadingInputSubject.observeOn(schedulerFactory.main()).subscribe(this::getBookUserReading)
        );
    }

    @Override
    public void onDestroy() {
        bookUserSharingDisposable.dispose();
    }

    @Override
    public void requestBookUserSharing(BookSharingUserInput input) {
        bookUserSharingInputSubject.onNext(input);
    }

    @Override
    public Observable<Data> getResponseBookUserSharing() {
        return bookUserSharingResultSubject.observeOn(schedulerFactory.main());
    }

    @Override
    public Observable<ServerResponse<ResponseData>> onErrorBookUserSharing() {
        return bookUserSharingError.observeOn(schedulerFactory.main());

    }

    @Override
    public void requestBookUserReading(BookReadingInput input) {
        bookUserReadingInputSubject.onNext(input);
    }

    @Override
    public Observable<Data> getResponseBookUserReading() {
        return bookUserReadingResultSubject.observeOn(schedulerFactory.main());
    }

    @Override
    public Observable<ServerResponse<ResponseData>> onErrorBookUserReading() {
        return bookUserReadingError.observeOn(schedulerFactory.main());
    }

    private void getBookUserSharing(BookSharingUserInput input) {
        bookUserSharingUsecase = UseCases.release(bookUserSharingUsecase);
        bookUserSharingUsecase = useCaseFactory.getBookUserSharing(input);
        bookUserSharingUsecase.executeOn(schedulerFactory.io())
                .returnOn(schedulerFactory.main())
                .onNext(response -> {
                    bookUserSharingResultSubject.onNext(response);
                })
                .onError(throwable -> {
                    bookUserSharingError.onError(throwable);
                })
                .onStop(
                        () -> bookUserSharingUsecase = UseCases.release(bookUserSharingUsecase)
                )
                .execute();
    }


    private void getBookUserReading(BookReadingInput input) {
        bookUserReadingUsecase = UseCases.release(bookUserReadingUsecase);
        bookUserReadingUsecase = useCaseFactory.getReadingBooks(input);
        bookUserReadingUsecase.executeOn(schedulerFactory.io())
                .returnOn(schedulerFactory.main())
                .onNext(response -> {
                    bookUserReadingResultSubject.onNext(response);
                })
                .onError(throwable -> {
                    bookUserReadingError.onError(throwable);
                })
                .onStop(
                        () -> bookUserReadingUsecase = UseCases.release(bookUserReadingUsecase)
                )
                .execute();
    }
}
