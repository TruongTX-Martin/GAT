package com.gat.feature.personaluser;

import com.gat.data.exception.CommonException;
import com.gat.data.exception.LoginException;
import com.gat.data.response.ResponseData;
import com.gat.data.response.ServerResponse;
import com.gat.domain.SchedulerFactory;
import com.gat.domain.UseCaseFactory;
import com.gat.domain.UseCases;
import com.gat.domain.usecase.UseCase;
import com.gat.feature.bookdetailsender.entity.ChangeStatusResponse;
import com.gat.feature.personal.entity.BookReadingInput;
import com.gat.feature.personaluser.entity.BookSharingUserInput;
import com.gat.feature.personaluser.entity.BorrowRequestInput;
import com.gat.repository.entity.Data;
import com.gat.repository.entity.User;

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

    //request borrow book
    private CompositeDisposable requestBorrowBookDisposable;
    private UseCase<Data> requestBorrowBookUsecase;
    private Subject<Data> requestBorrowBookResultSubject;
    private Subject<BorrowRequestInput> requestBorrowBookInputSubject;
    private Subject<String> requestBorrowBookError;

    //request visitor information
    private CompositeDisposable visitorInfoDisposable;
    private final Subject<User> visitorInfoResultSubject;
    private final Subject<Integer> visitorInfoInputSubject;
    private final Subject<ServerResponse<ResponseData>> visitorInfoError;
    private UseCase<User> getVisitorUsecase;

    public PersonalUserPresenterImpl(UseCaseFactory useCaseFactory, SchedulerFactory factory) {
        this.useCaseFactory = useCaseFactory;
        this.schedulerFactory = factory;


        this.bookUserSharingError = PublishSubject.create();
        bookUserSharingResultSubject = PublishSubject.create();
        bookUserSharingInputSubject = BehaviorSubject.create();

        this.bookUserReadingError = PublishSubject.create();
        bookUserReadingResultSubject = PublishSubject.create();
        bookUserReadingInputSubject = BehaviorSubject.create();

        this.requestBorrowBookError = PublishSubject.create();
        requestBorrowBookResultSubject = PublishSubject.create();
        requestBorrowBookInputSubject = BehaviorSubject.create();

        this.visitorInfoError = PublishSubject.create();
        visitorInfoResultSubject = PublishSubject.create();
        visitorInfoInputSubject = BehaviorSubject.create();


    }

    @Override
    public void onCreate() {
        bookUserSharingDisposable = new CompositeDisposable(
                bookUserSharingInputSubject.observeOn(schedulerFactory.main()).subscribe(this::getBookUserSharing)
        );

        bookUserReadingDisposable = new CompositeDisposable(
                bookUserReadingInputSubject.observeOn(schedulerFactory.main()).subscribe(this::getBookUserReading)
        );

        requestBorrowBookDisposable = new CompositeDisposable(
                requestBorrowBookInputSubject.observeOn(schedulerFactory.main()).subscribe(this::requestBook)
        );

        visitorInfoDisposable = new CompositeDisposable(
                visitorInfoInputSubject.observeOn(schedulerFactory.main()).subscribe(this::getVisitorInfo)
        );
    }

    @Override
    public void onDestroy() {
        bookUserSharingDisposable.dispose();
        bookUserReadingDisposable.dispose();
        requestBorrowBookDisposable.dispose();
        visitorInfoDisposable.dispose();
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

    @Override
    public void requestBorrowBook(BorrowRequestInput input) {
        requestBorrowBookInputSubject.onNext(input);
    }

    @Override
    public Observable<Data> getResponseBorrowBook() {
        return requestBorrowBookResultSubject.observeOn(schedulerFactory.main());
    }

    @Override
    public Observable<String> onErrorBorrowBook() {
        return requestBorrowBookError.observeOn(schedulerFactory.main());
    }

    @Override
    public void requestVisitorInfo(int userId) {
        visitorInfoInputSubject.onNext(userId);
    }

    @Override
    public Observable<User> getResponseVisitorInfo() {
        return visitorInfoResultSubject.observeOn(schedulerFactory.main());
    }

    @Override
    public Observable<ServerResponse<ResponseData>> onErrorVisitorInfo() {
        return visitorInfoError.observeOn(schedulerFactory.main());
    }

    private void getVisitorInfo(int userId) {
        getVisitorUsecase = UseCases.release(getVisitorUsecase);
        getVisitorUsecase = useCaseFactory.getVisitorInfor(userId);
        getVisitorUsecase.executeOn(schedulerFactory.io())
                .returnOn(schedulerFactory.main())
                .onNext(response -> {
                    visitorInfoResultSubject.onNext(response);
                })
                .onError(throwable -> {
                })
                .onStop(
                        () -> getVisitorUsecase = UseCases.release(getVisitorUsecase)
                )
                .execute();
    }

    private void requestBook(BorrowRequestInput input){
        requestBorrowBookUsecase = UseCases.release(requestBorrowBookUsecase);
        requestBorrowBookUsecase = useCaseFactory.requestBorrowBook(input);
        requestBorrowBookUsecase.executeOn(schedulerFactory.io())
                .returnOn(schedulerFactory.main())
                .onNext(response -> {
                    requestBorrowBookResultSubject.onNext(response);
                })
                .onError(throwable -> {
                   if (throwable instanceof CommonException)
                       requestBorrowBookError.onNext(((CommonException)throwable).getMessage());
                    else
                       requestBorrowBookError.onNext(ServerResponse.EXCEPTION.message());
//                    if (throwable instanceof CommonException)
//                        requestBorrowBookError.onNext(((CommonException)throwable).getMessage());
//                    else {
//                        throwable.printStackTrace();
//                        requestBorrowBookError.onNext("Exception occurred.");
//                    }
                })
                .onStop(
                        () -> requestBorrowBookUsecase = UseCases.release(requestBorrowBookUsecase)
                )
                .execute();
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
