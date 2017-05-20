package com.gat.feature.book_detail.list_user_sharing_book;

import android.util.Log;

import com.gat.common.util.MZDebug;
import com.gat.data.exception.CommonException;
import com.gat.data.exception.LoginException;
import com.gat.data.response.ServerResponse;
import com.gat.data.response.UserResponse;
import com.gat.data.response.impl.BorrowResponse;
import com.gat.domain.SchedulerFactory;
import com.gat.domain.UseCaseFactory;
import com.gat.domain.usecase.UseCase;
import com.gat.repository.entity.User;
import java.util.List;
import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

/**
 * Created by mozaa on 21/04/2017.
 */

public class ListUserSharingBookPresenterImpl implements ListUserSharingBookPresenter {

    private final UseCaseFactory useCaseFactory;
    private final SchedulerFactory schedulerFactory;

    private final Subject<Integer> subjectUserIdSuccess;
    private final Subject<String> subjectUserIdFailure;

    private UseCase<BorrowResponse> useCaseRequestBorrowBook;
    private final Subject<BorrowResponse> subjectBorrowResponse;
    private final Subject<String> subjectRequestBorrowBookFailure;

    private final Subject<String> subjectUnAuthorization;

    private List<UserResponse> mListUser;

    public ListUserSharingBookPresenterImpl(UseCaseFactory useCaseFactory, SchedulerFactory schedulerFactory) {
        this.useCaseFactory = useCaseFactory;
        this.schedulerFactory = schedulerFactory;
        subjectUserIdSuccess = PublishSubject.create();
        subjectUserIdFailure = PublishSubject.create();
        subjectBorrowResponse = PublishSubject.create();
        subjectRequestBorrowBookFailure = PublishSubject.create();
        subjectUnAuthorization = PublishSubject.create();
    }

    @Override
    public void onCreate() {
        MZDebug.e("++++++++++++++++++++++++++++ PRESENTER onCreate +++++++++++++++++++++++++++++++++");
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public void setListUser(List<UserResponse> list) {
        mListUser = list;
        MZDebug.w("Presenter:setListUser : 0 = " + list.get(0).toString());
    }

    @Override
    public void getUserId() {
        UseCase<User> getUser = useCaseFactory.getUser();
        getUser.executeOn(schedulerFactory.io())
                .returnOn(schedulerFactory.main())
                .onNext(user -> {
                    if (user != null) {
                        subjectUserIdSuccess.onNext(user.userId());
                    } else {
                        subjectUserIdFailure.onNext("Failed");
                    }
                })
                .onError(throwable -> {
                    subjectUserIdFailure.onNext("Failed");
                }).execute();
    }

    @Override
    public Observable<Integer> onUserIdSuccess() {
        return subjectUserIdSuccess.subscribeOn(schedulerFactory.main());
    }

    @Override
    public Observable<String> onUserIdFailure() {
        return subjectUserIdFailure.subscribeOn(schedulerFactory.main());
    }

    @Override
    public void requestBorrowBook(int editionId, int ownerId) {
        useCaseRequestBorrowBook = useCaseFactory.requestBorrow(editionId, ownerId);
        useCaseRequestBorrowBook.executeOn(schedulerFactory.io())
                .returnOn(schedulerFactory.main())
                .onNext(borrowResponse -> {
                    if (null != borrowResponse) {
                        subjectBorrowResponse.onNext(borrowResponse);
                    } else {
                        subjectRequestBorrowBookFailure.onNext(ServerResponse.BAD_RESPONSE.message());
                    }
                })
                .onError(throwable -> {
                    MZDebug.e("ERROR: requestBorrowBook ____________________________________ E \n\r"
                            + Log.getStackTraceString(throwable));

                    if (throwable instanceof LoginException) {
                        LoginException exception = (LoginException) throwable;
                        subjectUnAuthorization.onNext(exception.responseData().message());
                    } else if (throwable instanceof CommonException) {
                        subjectRequestBorrowBookFailure.onNext(((CommonException) throwable).getMessage());
                    } else {
                        subjectRequestBorrowBookFailure.onNext(ServerResponse.EXCEPTION.message());
                    }
                })
                .execute();
    }

    @Override
    public Observable<BorrowResponse> onRequestBorrowBookSuccess() {
        return subjectBorrowResponse.subscribeOn(schedulerFactory.main());
    }

    @Override
    public Observable<String> onRequestBorrowBookFailure() {
        return subjectRequestBorrowBookFailure.subscribeOn(schedulerFactory.main());
    }

    @Override
    public Observable<String> onUnAuthorization() {
        return subjectUnAuthorization.subscribeOn(schedulerFactory.main());
    }


}
