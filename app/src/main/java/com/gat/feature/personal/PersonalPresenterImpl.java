package com.gat.feature.personal;

import android.util.Log;

import com.gat.common.util.MZDebug;
import com.gat.data.exception.LoginException;
import com.gat.data.exception.CommonException;
import com.gat.data.response.ResponseData;
import com.gat.data.response.ServerResponse;
import com.gat.domain.SchedulerFactory;
import com.gat.domain.UseCaseFactory;
import com.gat.domain.UseCases;
import com.gat.domain.usecase.UseCase;
import com.gat.feature.bookdetailsender.entity.ChangeStatusResponse;
import com.gat.feature.personal.entity.BookInstanceInput;
import com.gat.feature.personal.entity.BookChangeStatusInput;
import com.gat.feature.personal.entity.BookReadingInput;
import com.gat.feature.personal.entity.BookRequestInput;
import com.gat.feature.personal.entity.RequestStatusInput;
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
    private final Subject<ServerResponse<ResponseData>> personalError;
    private UseCase<Data<User>> getPersonalUsecase;

    //get book instance
    private CompositeDisposable bookInstanceDisposable;
    private UseCase<Data> getBookIntanceUsecase;
    private Subject<Data> bookInstanceResultSubject;
    private Subject<BookInstanceInput> bookInstanceInputSubject;
    private final Subject<ServerResponse<ResponseData>> bookInstanceError;


    //change book sharing status
    private CompositeDisposable changeBookSharingStatusDisposable;
    private UseCase<String> changeBookSharingStatusUsecase;
    private Subject<String> changeBookSharingStatusResultSubject;
    private Subject<BookChangeStatusInput> changeBookSharingStatusInputSubject;
    private Subject<String> changeBookSharingStatusError;

    //get data readingbook
    private CompositeDisposable readingBooksDisposable;
    private UseCase<Data> readingBooksUsecase;
    private Subject<Data> readingBookResultSubject;
    private Subject<BookReadingInput> readingBookInputSubject;
    private Subject<ServerResponse<ResponseData>> readingBookError;

    //get data book request
    private CompositeDisposable requestBooksDisposable;
    private UseCase<Data> requestBooksUsecase;
    private Subject<Data> requestBookResultSubject;
    private Subject<BookRequestInput> requestBookInputSubject;
    private Subject<ServerResponse<ResponseData>> requestBookError;


    //change status book
    private CompositeDisposable changeStatusDisposable;
    private final Subject<ChangeStatusResponse> changeStatusResultSubject;
    private final Subject<RequestStatusInput> changeStatusInputSubject;
    private final Subject<String> changeStatusError;
    private UseCase<ChangeStatusResponse> changeStatusUsecase;


    //check login
    private CompositeDisposable checkLoginDisposable;
    private final Subject<String> checkLoginResultSubject;
    private final Subject<String> checkLoginInputSubject;
    private final Subject<String> checkLoginError;
    private final UseCase<User> loadLocalUser;

    //request borrow book
    private CompositeDisposable requestRemoveBookDisposable;
    private  UseCase<String> removeBookUsecase;
    private final Subject<String> removeBookResultSubject;
    private final Subject<Integer> removeBookInputSubject;
    private final Subject<String> removeBookError;

    private boolean isLogin;

    public PersonalPresenterImpl(UseCaseFactory useCaseFactory, SchedulerFactory factory) {
        this.useCaseFactory = useCaseFactory;
        this.schedulerFactory = factory;

        this.removeBookError = PublishSubject.create();
        removeBookResultSubject = PublishSubject.create();
        removeBookInputSubject = BehaviorSubject.create();

        this.personalError = PublishSubject.create();
        personalResultSubject = PublishSubject.create();
        personalInputSubject = BehaviorSubject.create();

        this.checkLoginError = PublishSubject.create();
        checkLoginResultSubject = PublishSubject.create();
        checkLoginInputSubject = BehaviorSubject.create();


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


        this.changeStatusError = PublishSubject.create();
        changeStatusResultSubject = PublishSubject.create();
        changeStatusInputSubject = BehaviorSubject.create();

        loadLocalUser = useCaseFactory.getUser();

        isLogin = false;
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

        changeStatusDisposable = new CompositeDisposable(
                changeStatusInputSubject.observeOn(schedulerFactory.main()).subscribe(this::getChangeStatus)
        );

        requestRemoveBookDisposable = new CompositeDisposable(
                removeBookInputSubject.observeOn(schedulerFactory.main()).subscribe(this::removeBookInstance)
        );

        checkLoginDisposable = new CompositeDisposable(checkLoginInputSubject.
                observeOn(schedulerFactory.main()).subscribe(this::startCheckLogin));
        // TODO 170506 do not start get personal information here
        //start get personal data
        loadLocalUser.executeOn(schedulerFactory.io())
                .returnOn(schedulerFactory.main())
                .onNext(user -> {
                    MZDebug.w("local login: " + user.isValid());
                    if (user.isValid()) {
                        isLogin = true;
                    } else {
                        isLogin = false;
                    }
                })
                .onError(throwable -> {
                    MZDebug.e("ERROR: suggestBooks : get local login data___________________ E \n\r"
                            + Log.getStackTraceString(throwable));
                })
                .execute();
    }


    @Override
    public void onDestroy() {
        personalDisposable.dispose();
        bookInstanceDisposable.dispose();
        changeBookSharingStatusDisposable.dispose();
        requestBooksDisposable.dispose();
        changeStatusDisposable.dispose();
        checkLoginDisposable.dispose();
        requestRemoveBookDisposable.dispose();
    }

    @Override
    public void requestChangeStatus(RequestStatusInput input) {
        if (isLogin)
            changeStatusInputSubject.onNext(input);
    }

    @Override
    public Observable<ChangeStatusResponse> getResponseChangeStatus() {
        return changeStatusResultSubject.observeOn(schedulerFactory.main());
    }

    @Override
    public Observable<String> onErrorChangeStatus() {
        return changeStatusError.observeOn(schedulerFactory.main());
    }

    @Override
    public void checkLogin() {
//        if(!isLogin){
//            showDialogLogin();
//        }
        checkLoginInputSubject.onNext("");
    }

    @Override
    public Observable<String> checkLoginSucess() {
        return checkLoginResultSubject.observeOn(schedulerFactory.main());
    }

    @Override
    public Observable<String> checkLoginFailed() {
        return checkLoginError.observeOn(schedulerFactory.main());
    }

    @Override
    public void removeBook(int instanceId) {
        removeBookInputSubject.onNext(instanceId);
    }

    @Override
    public Observable<String> getResponseRemoveBook() {
        return removeBookResultSubject.observeOn(schedulerFactory.main());
    }

    @Override
    public Observable<String> onErrorRemoveBook() {
        return removeBookError.observeOn(schedulerFactory.main());
    }

    @Override
    public void requestPersonalInfor(String input) {
        if (isLogin)
            personalInputSubject.onNext("");
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
        if (isLogin)
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
    public void requestChangeBookSharingStatus(BookChangeStatusInput input) {
        if (isLogin)
            changeBookSharingStatusInputSubject.onNext(input);
    }

    @Override
    public Observable<String> getResponseBookSharingStatus() {
        return changeBookSharingStatusResultSubject.observeOn(schedulerFactory.main());
    }

    @Override
    public Observable<String> onErrorBookSharingStatus() {
        return changeBookSharingStatusError.observeOn(schedulerFactory.main());
    }

    @Override
    public void requestReadingBooks(BookReadingInput input) {
        if (isLogin)
            readingBookInputSubject.onNext(input);

    }

    @Override
    public Observable<Data> getResponseReadingBooks() {
        return readingBookResultSubject.observeOn(schedulerFactory.main());
    }

    @Override
    public Observable<ServerResponse<ResponseData>> onErrorReadingBooks() {
        return readingBookError.observeOn(schedulerFactory.main());
    }

    @Override
    public void requestBookRequests(BookRequestInput input) {
        if (isLogin) {
            requestBookInputSubject.onNext(input);
        }
    }

    @Override
    public Observable<Data> getResponseBookRequest() {
        return requestBookResultSubject.observeOn(schedulerFactory.main());
    }

    @Override
    public Observable<ServerResponse<ResponseData>> onErrorBookRequest() {
        return requestBookError.observeOn(schedulerFactory.main());
    }

    private void startCheckLogin(String input){
        if(isLogin) {
            checkLoginResultSubject.onNext("");
        }else{
            checkLoginError.onNext("");
        }
    }

    private void removeBookInstance(int instanceId) {
        removeBookUsecase = UseCases.release(removeBookUsecase);
        removeBookUsecase = useCaseFactory.removeBook(instanceId);
        removeBookUsecase.executeOn(schedulerFactory.io())
                .returnOn(schedulerFactory.main())
                .onNext(response -> {
                    removeBookResultSubject.onNext(response);
                })
                .onError(throwable -> {
                    if (throwable instanceof CommonException)
                        changeStatusError.onNext(((CommonException)throwable).getMessage());
                    else
                        changeStatusError.onNext(ServerResponse.EXCEPTION.message());
                })
                .onStop(
                        () -> removeBookUsecase = UseCases.release(removeBookUsecase)
                )
                .execute();
    }

    private void getChangeStatus(RequestStatusInput input){
        changeStatusUsecase = UseCases.release(changeStatusUsecase);
        changeStatusUsecase = useCaseFactory.requestBookByOwner(input);
        changeStatusUsecase.executeOn(schedulerFactory.io())
                .returnOn(schedulerFactory.main())
                .onNext(response -> {
                    changeStatusResultSubject.onNext(response);
                })
                .onError(throwable -> {
                    if (throwable instanceof CommonException)
                        changeStatusError.onNext(((CommonException)throwable).getMessage());
                    else
                        changeStatusError.onNext(ServerResponse.EXCEPTION.message());
                })
                .onStop(
                        () -> changeStatusUsecase = UseCases.release(changeStatusUsecase)
                )
                .execute();
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
                    if (throwable instanceof LoginException)
                        readingBookError.onNext(ServerResponse.TOKEN_CHANGED);
                    else
                        requestBookError.onNext(ServerResponse.EXCEPTION);
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
                    if (throwable instanceof LoginException)
                        readingBookError.onNext(ServerResponse.TOKEN_CHANGED);
                    else
                        personalError.onNext(ServerResponse.EXCEPTION);
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
                    if (throwable instanceof LoginException)
                        readingBookError.onNext(ServerResponse.TOKEN_CHANGED);
                    else
                        bookInstanceError.onNext(ServerResponse.EXCEPTION);
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
                    else
                        changeBookSharingStatusError.onNext(ServerResponse.EXCEPTION.message());
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
                    if (throwable instanceof LoginException)
                        readingBookError.onNext(ServerResponse.TOKEN_CHANGED);
                    else
                        readingBookError.onNext(ServerResponse.EXCEPTION);
                })
                .onStop(
                        () -> readingBooksUsecase = UseCases.release(readingBooksUsecase)
                )
                .execute();
    }
}
