package com.gat.feature.book_detail.add_to_bookcase;

import android.util.Log;

import com.gat.common.util.MZDebug;
import com.gat.data.exception.CommonException;
import com.gat.data.exception.LoginException;
import com.gat.data.response.ServerResponse;
import com.gat.data.response.impl.BookInfo;
import com.gat.data.response.impl.BookInstanceInfo;
import com.gat.domain.SchedulerFactory;
import com.gat.domain.UseCaseFactory;
import com.gat.domain.usecase.UseCase;
import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

/**
 * Created by mryit on 4/22/2017.
 */

public class AddToBookcasePresenterImpl implements AddToBookcasePresenter {

    private final UseCaseFactory useCaseFactory;
    private final SchedulerFactory schedulerFactory;

    private UseCase<BookInstanceInfo> bookInstanceInfoUseCase;
    private final Subject<BookInstanceInfo> subjectBookTotalInstance;

    private UseCase<ServerResponse> addBookInstanceUseCase;
    private final Subject<String> subjectAddBookInstance;
    private final Subject<String> subjectAddBookFailure;
    private final Subject<String> subjectUnAuthorization;

    private BookInfo mBookInfo;
    private Integer mReadingId;

    public AddToBookcasePresenterImpl(UseCaseFactory useCaseFactory, SchedulerFactory schedulerFactory) {
        this.useCaseFactory = useCaseFactory;
        this.schedulerFactory = schedulerFactory;
        subjectBookTotalInstance = PublishSubject.create();
        subjectAddBookInstance = PublishSubject.create();
        subjectAddBookFailure = PublishSubject.create();
        subjectUnAuthorization = PublishSubject.create();
    }


    @Override
    public void onCreate() {

    }

    @Override
    public void onDestroy() {

    }


    @Override
    public void setBookInfo(BookInfo bookInfo) {
        mBookInfo = bookInfo;
    }

    @Override
    public void setReadingId(Integer readingId) {
        mReadingId = readingId;
    }

    @Override
    public void getBookTotalInstance() {
        bookInstanceInfoUseCase = useCaseFactory.getSelfInstanceInfo(mBookInfo.getEditionId());
        bookInstanceInfoUseCase.executeOn(schedulerFactory.io())
                .returnOn(schedulerFactory.main())
                .onNext(bookInstanceInfo -> {
                    subjectBookTotalInstance.onNext(bookInstanceInfo);
                })
                .onError(throwable -> {
                    MZDebug.e("ERROR: getBookTotalInstance _________________________________ E \n\r"
                            + Log.getStackTraceString(throwable));

                    if (throwable instanceof LoginException) {
                        LoginException exception = (LoginException) throwable;
                        subjectUnAuthorization.onNext(exception.responseData().message());
                    } else if (throwable instanceof CommonException){
                        subjectAddBookFailure.onNext( throwable.getMessage() );
                    } else {
                        subjectAddBookFailure.onNext( ServerResponse.EXCEPTION.message() );
                    }

                }).execute();
    }

    @Override
    public Observable<BookInstanceInfo> onGetBookTotalInstanceSuccess() {
        return subjectBookTotalInstance.subscribeOn(schedulerFactory.main());
    }

    @Override
    public void addBookInstance(int sharingStatus, int numberOfBook) {
        addBookInstanceUseCase = useCaseFactory.selfAddInstance(mBookInfo.getEditionId(), sharingStatus, numberOfBook, mBookInfo.getBookId(), mReadingId);
        addBookInstanceUseCase.executeOn(schedulerFactory.io())
                .returnOn(schedulerFactory.main())
                .onNext(serverResponse -> {
                    subjectAddBookInstance.onNext(serverResponse.message());
                })
                .onError(throwable -> {
                    MZDebug.e("ERROR: addBookInstance ______________________________________ E \n\r"
                            + Log.getStackTraceString(throwable));

                    if (throwable instanceof LoginException) {
                        LoginException exception = (LoginException) throwable;
                        subjectUnAuthorization.onNext(exception.responseData().message());
                    } else if (throwable instanceof CommonException){
                        subjectAddBookFailure.onNext( throwable.getMessage() );
                    } else {
                        subjectAddBookFailure.onNext( ServerResponse.EXCEPTION.message() );
                    }
                })
                .execute();
    }

    @Override
    public Observable<String> onAddBookInstanceSuccess() {
        return subjectAddBookInstance.subscribeOn(schedulerFactory.main());
    }

    @Override
    public Observable<String> onAddBookInstanceFailure() {
        return subjectAddBookFailure.subscribeOn(schedulerFactory.main());
    }

    @Override
    public Observable<String> onUnAuthorization() {
        return subjectUnAuthorization.subscribeOn(schedulerFactory.main());
    }


}
