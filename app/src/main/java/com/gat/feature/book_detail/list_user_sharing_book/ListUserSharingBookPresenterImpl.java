package com.gat.feature.book_detail.list_user_sharing_book;

import android.util.Log;

import com.gat.common.util.MZDebug;
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

    private List<UserResponse> mListUser;

    public ListUserSharingBookPresenterImpl(UseCaseFactory useCaseFactory, SchedulerFactory schedulerFactory) {
        this.useCaseFactory = useCaseFactory;
        this.schedulerFactory = schedulerFactory;
        subjectUserIdSuccess = PublishSubject.create();
        subjectUserIdFailure = PublishSubject.create();
        subjectBorrowResponse = PublishSubject.create();
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
    }

    @Override
    public void getUserId() {
        UseCase<User> getUser = useCaseFactory.getUser();
        getUser.executeOn(schedulerFactory.io())
                .returnOn(schedulerFactory.main())
                .onNext(user -> {
                    subjectUserIdSuccess.onNext(2);
                })
                .onError(throwable -> {
                    subjectUserIdFailure.onNext("");
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
                    subjectBorrowResponse.onNext(borrowResponse);
                })
                .onError(throwable -> {
                    MZDebug.e("ERROR: requestBorrowBook ____________________________________ E \n\r"
                            + Log.getStackTraceString(throwable));
                })
                .execute();

    }

    @Override
    public Observable<BorrowResponse> onRequestBorrowBookSuccess() {
        return subjectBorrowResponse.subscribeOn(schedulerFactory.main());
    }


}
