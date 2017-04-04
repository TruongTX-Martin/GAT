package com.gat.feature.personal;

import com.gat.data.response.ResponseData;
import com.gat.data.response.ServerResponse;
import com.gat.domain.SchedulerFactory;
import com.gat.domain.UseCaseFactory;
import com.gat.domain.UseCases;
import com.gat.domain.usecase.UseCase;
import com.gat.feature.personal.entity.BookInstanceInput;
import com.gat.feature.personal.entity.BookSharingInput;
import com.gat.repository.entity.Data;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

/**
 * Created by truongtechno on 23/03/2017.
 */

public class PersonalPresenterImpl implements PersonalPresenter {


    private UseCaseFactory useCaseFactory;
    private SchedulerFactory schedulerFactory;

    //get personal
    private CompositeDisposable personalDisposable;
    private final Subject<Data> personalResultSubject;
    private final Subject<String> personalInputSubject;
    private final Subject<ServerResponse<ResponseData>> personalError;
    private UseCase<Data> getPersonalUsecase;

    //get book instance
    private CompositeDisposable bookInstanceDisposable;
    private UseCase<Data> getBookIntanceUsecase;
    private Subject<Data> bookInstanceResultSubject;
    private Subject<BookInstanceInput> bookInstanceInputSubject;
    private final Subject<ServerResponse<ResponseData>> bookInstanceError;


    //change book sharing status
    private CompositeDisposable changeBookSharingStatusDisposable;
    private UseCase<Data> changeBookSharingStatusUsecase;
    private Subject<Data> changeBookSharingStatusResultSubject;
    private Subject<BookInstanceInput> changeBookSharingStatusInputSubject;
    private  Subject<ServerResponse<ResponseData>> changeBookSharingStatusError;


    public PersonalPresenterImpl(UseCaseFactory useCaseFactory, SchedulerFactory factory) {
        this.useCaseFactory = useCaseFactory;
        this.schedulerFactory = factory;

        this.personalError = PublishSubject.create();
        personalResultSubject = PublishSubject.create();
        personalInputSubject = BehaviorSubject.create();


        this.bookInstanceError = PublishSubject.create();
        bookInstanceResultSubject = PublishSubject.create();
        bookInstanceInputSubject = BehaviorSubject.create();

        this.changeBookSharingStatusError = PublishSubject.create();
        changeBookSharingStatusResultSubject = PublishSubject.create();
        changeBookSharingStatusInputSubject = BehaviorSubject.create();
    }

    @Override
    public void onCreate() {
        personalDisposable = new CompositeDisposable(
                personalInputSubject.observeOn(schedulerFactory.main()).subscribe(this::getPersonalData)
        );
        bookInstanceDisposable = new CompositeDisposable(bookInstanceInputSubject.
                observeOn(schedulerFactory.main()).subscribe(this::getBookInstance));
        //start get personal data
        personalInputSubject.onNext("");
    }

    @Override
    public void onDestroy() {
        personalDisposable.dispose();
        bookInstanceDisposable.dispose();
    }

    @Override
    public Observable<Data> getResponsePersonal() {
        return personalResultSubject.observeOn(schedulerFactory.main());
    }

    @Override
    public Observable<ServerResponse<ResponseData>> onErrorPersonal() {
        return personalError.observeOn(schedulerFactory.main());
    }

    @Override
    public void requestBookInstance(BookInstanceInput input) {
        bookInstanceInputSubject.onNext(input);
    }

    @Override
    public Observable<Data> getResponseBookInstance() {
        return bookInstanceResultSubject.observeOn(schedulerFactory.main());
    }

    @Override
    public Observable<ServerResponse<ResponseData>> onErrorBookInstance() {
        return bookInstanceError.observeOn(schedulerFactory.main());
    }

    @Override
    public void requestChangeBookSharingStatus(BookSharingInput input) {
    }

    @Override
    public Observable<Data> getResponseBookSharingStatus() {
        return null;
    }

    @Override
    public Observable<ServerResponse<ResponseData>> onErrorBookSharingStatus() {
        return null;
    }

    private void getPersonalData(String input) {
        getPersonalUsecase = UseCases.release(getPersonalUsecase);
        getPersonalUsecase = useCaseFactory.getUserInfo();
        getPersonalUsecase.executeOn(schedulerFactory.io())
                .returnOn(schedulerFactory.main())
                .onNext(response -> {
                    personalResultSubject.onNext(response);
                })
                .onError(throwable -> {
                    personalError.onError(throwable);
                })
                .onStop(
                        () -> getPersonalUsecase = UseCases.release(getPersonalUsecase)
                )
                .execute();
    }

    private void getBookInstance(BookInstanceInput bookInstanceInput) {
        getBookIntanceUsecase = UseCases.release(getBookIntanceUsecase);
        getBookIntanceUsecase = useCaseFactory.getBookInstance(bookInstanceInput);
        getBookIntanceUsecase.executeOn(schedulerFactory.io()).returnOn(schedulerFactory.main()).
                onNext(reponse -> {
                    bookInstanceResultSubject.onNext(reponse);
                })
                .onError(throwableable -> {
                    bookInstanceError.onError(throwableable);
                }).onStop(
                () -> getBookIntanceUsecase = UseCases.release(getBookIntanceUsecase)
        ).execute();
    }
}
