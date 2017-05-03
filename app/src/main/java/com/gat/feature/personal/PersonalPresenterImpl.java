package com.gat.feature.personal;

import com.gat.data.exception.CommonException;
import com.gat.data.response.ResponseData;
import com.gat.data.response.ServerResponse;
import com.gat.data.user.PaperUserDataSource;
import com.gat.domain.SchedulerFactory;
import com.gat.domain.UseCaseFactory;
import com.gat.domain.UseCases;
import com.gat.domain.usecase.UseCase;
import com.gat.feature.personal.entity.BookInstanceInput;
import com.gat.feature.personal.entity.BookChangeStatusInput;
import com.gat.feature.personal.entity.BookReadingInput;
import com.gat.feature.personal.entity.BookRequestInput;
import com.gat.repository.entity.Data;
import com.gat.repository.entity.User;

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
    private final Subject<String> personalError;
    private UseCase<Data<User>> getPersonalUsecase;

    //get book instance
    private CompositeDisposable bookInstanceDisposable;
    private UseCase<Data> getBookIntanceUsecase;
    private Subject<Data> bookInstanceResultSubject;
    private Subject<BookInstanceInput> bookInstanceInputSubject;
    private final Subject<String> bookInstanceError;


    //change book sharing status
    private CompositeDisposable changeBookSharingStatusDisposable;
    private UseCase<Data> changeBookSharingStatusUsecase;
    private Subject<Data> changeBookSharingStatusResultSubject;
    private Subject<BookChangeStatusInput> changeBookSharingStatusInputSubject;
    private Subject<String> changeBookSharingStatusError;

    //get data readingbook
    private CompositeDisposable readingBooksDisposable;
    private UseCase<Data> readingBooksUsecase;
    private Subject<Data> readingBookResultSubject;
    private Subject<BookReadingInput> readingBookInputSubject;
    private Subject<String> readingBookError;

    //get data book request
    private CompositeDisposable requestBooksDisposable;
    private UseCase<Data> requestBooksUsecase;
    private Subject<Data> requestBookResultSubject;
    private Subject<BookRequestInput> requestBookInputSubject;
    private Subject<String> requestBookError;

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

        this.readingBookError = PublishSubject.create();
        readingBookResultSubject = PublishSubject.create();
        readingBookInputSubject = BehaviorSubject.create();

        this.requestBookError = PublishSubject.create();
        requestBookResultSubject = PublishSubject.create();
        requestBookInputSubject =  BehaviorSubject.create();
    }

    @Override
    public void onCreate() {
        personalDisposable = new CompositeDisposable(
                personalInputSubject.observeOn(schedulerFactory.main()).subscribe(this::getPersonalData)
        );
        bookInstanceDisposable = new CompositeDisposable(bookInstanceInputSubject.
                observeOn(schedulerFactory.main()).subscribe(this::getBookInstance));

        changeBookSharingStatusDisposable = new CompositeDisposable(changeBookSharingStatusInputSubject.
                observeOn(schedulerFactory.main()).subscribe(this::changeBookSharingStatus));

        readingBooksDisposable = new CompositeDisposable(readingBookInputSubject.
                observeOn(schedulerFactory.main()).subscribe(this::getReadingBooks));

        requestBooksDisposable = new CompositeDisposable(requestBookInputSubject.
                observeOn(schedulerFactory.main()).subscribe(this::getRequestBookData));
        //start get personal data
        personalInputSubject.onNext("");
    }

    @Override
    public void onDestroy() {
        personalDisposable.dispose();
        bookInstanceDisposable.dispose();
        changeBookSharingStatusDisposable.dispose();
        requestBooksDisposable.dispose();
    }

    @Override
    public void requestPersonalInfor(String input) {
        personalInputSubject.onNext("");
    }

    @Override
    public Observable<Data> getResponsePersonal() {
        return personalResultSubject.observeOn(schedulerFactory.main());
    }

    @Override
    public Observable<String> onErrorPersonal() {
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
    public Observable<String> onErrorBookInstance() {
        return bookInstanceError.observeOn(schedulerFactory.main());
    }

    @Override
    public void requestChangeBookSharingStatus(BookChangeStatusInput input) {
        changeBookSharingStatusInputSubject.onNext(input);
    }

    @Override
    public Observable<Data> getResponseBookSharingStatus() {
        return changeBookSharingStatusResultSubject.observeOn(schedulerFactory.main());
    }

    @Override
    public Observable<String> onErrorBookSharingStatus() {
        return changeBookSharingStatusError.observeOn(schedulerFactory.main());
    }

    @Override
    public void requestReadingBooks(BookReadingInput input) {
        readingBookInputSubject.onNext(input);
    }

    @Override
    public Observable<Data> getResponseReadingBooks() {
        return readingBookResultSubject.observeOn(schedulerFactory.main());
    }

    @Override
    public Observable<String> onErrorReadingBooks() {
        return readingBookError.observeOn(schedulerFactory.main());
    }

    @Override
    public void requestBookRequests(BookRequestInput input) {
        requestBookInputSubject.onNext(input);
    }

    @Override
    public Observable<Data> getResponseBookRequest() {
        return requestBookResultSubject.observeOn(schedulerFactory.main());
    }

    @Override
    public Observable<String> onErrorBookRequest() {
        return requestBookError.observeOn(schedulerFactory.main());
    }

    private void getRequestBookData(BookRequestInput input) {
        requestBooksUsecase = UseCases.release(requestBooksUsecase);
        requestBooksUsecase = useCaseFactory.getBookRequest(input);
        requestBooksUsecase.executeOn(schedulerFactory.io())
                .returnOn(schedulerFactory.main())
                .onNext(response -> {
                    requestBookResultSubject.onNext(response);
                })
                .onError(throwable -> {
                    if (throwable instanceof CommonException)
                        requestBookError.onNext(((CommonException)throwable).getMessage());
                    else {
                        throwable.printStackTrace();
                        requestBookError.onNext("Exception occurred.");
                    }
                })
                .onStop(
                        () -> requestBooksUsecase = UseCases.release(requestBooksUsecase)
                )
                .execute();
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
                    if (throwable instanceof CommonException)
                        personalError.onNext(((CommonException)throwable).getMessage());
                    else {
                        throwable.printStackTrace();
                        personalError.onNext("Exception occurred.");
                    }
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
                .onError(throwable -> {
                    if (throwable instanceof CommonException)
                        bookInstanceError.onNext(((CommonException)throwable).getMessage());
                    else {
                        throwable.printStackTrace();
                        bookInstanceError.onNext("Exception occurred.");
                    }
                }).onStop(
                () -> getBookIntanceUsecase = UseCases.release(getBookIntanceUsecase)
        ).execute();
    }

    private void changeBookSharingStatus(BookChangeStatusInput input) {
        changeBookSharingStatusUsecase = UseCases.release(changeBookSharingStatusUsecase);
        changeBookSharingStatusUsecase = useCaseFactory.changeBookSharingStatus(input);
        changeBookSharingStatusUsecase.executeOn(schedulerFactory.io()).returnOn(schedulerFactory.main()).
                onNext(reponse -> {
                    changeBookSharingStatusResultSubject.onNext(reponse);
                })
                .onError(throwable -> {
                    if (throwable instanceof CommonException)
                        changeBookSharingStatusError.onNext(((CommonException)throwable).getMessage());
                    else {
                        throwable.printStackTrace();
                        changeBookSharingStatusError.onNext("Exception occurred.");
                    }
                })
                .onStop(
                        () -> changeBookSharingStatusUsecase = UseCases.release(changeBookSharingStatusUsecase)
                )
                .execute();
    }

    private void getReadingBooks(BookReadingInput input){
        readingBooksUsecase = UseCases.release(readingBooksUsecase);
        readingBooksUsecase = useCaseFactory.getReadingBooks(input);
        readingBooksUsecase.executeOn(schedulerFactory.io()).returnOn(schedulerFactory.main()).
                onNext(reponse -> {
                    readingBookResultSubject.onNext(reponse);
                })
                .onError(throwable -> {
                    if (throwable instanceof CommonException)
                        readingBookError.onNext(((CommonException)throwable).getMessage());
                    else {
                        throwable.printStackTrace();
                        readingBookError.onNext("Exception occurred.");
                    }
                })
                .onStop(
                        () -> readingBooksUsecase = UseCases.release(readingBooksUsecase)
                )
                .execute();
    }
}
