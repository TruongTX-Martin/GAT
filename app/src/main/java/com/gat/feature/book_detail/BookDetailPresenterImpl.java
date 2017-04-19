package com.gat.feature.book_detail;

import android.util.Log;

import com.gat.common.util.MZDebug;
import com.gat.data.response.UserResponse;
import com.gat.data.response.impl.BookInfo;
import com.gat.data.response.impl.BookReadingInfo;
import com.gat.data.response.impl.EvaluationItemResponse;
import com.gat.domain.SchedulerFactory;
import com.gat.domain.UseCaseFactory;
import com.gat.domain.usecase.GetBookEditionEvaluation;
import com.gat.domain.usecase.GetBookEvaluationByUser;
import com.gat.domain.usecase.GetBookInfo;
import com.gat.domain.usecase.GetEditionSharingUser;
import com.gat.domain.usecase.GetReadingStatus;
import com.gat.domain.usecase.UseCase;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

/**
 * Created by mryit on 4/16/2017.
 */

public class BookDetailPresenterImpl implements BookDetailPresenter {

    private final UseCaseFactory useCaseFactory;
    private final SchedulerFactory schedulerFactory;

    private UseCase<BookInfo> useCaseGetBookInfo;
    private final Subject<BookInfo> subjectBookInfo;

    private UseCase<List<EvaluationItemResponse>> useCaseGetBookEditionEvaluation;
    private final Subject<List<EvaluationItemResponse>> subjectBookEditionEvaluation;

    private UseCase<BookReadingInfo> useCaseGetReadingStatus;
    private final Subject<BookReadingInfo> subjectReadingStatus;

    private UseCase<List<UserResponse>> useCaseGetEditionSharingUsers;
    private final Subject<List<UserResponse>> subjectEditionSharingUsers;

    private UseCase<EvaluationItemResponse> useCaseGetBookEvaluationByUser;
    private final Subject<EvaluationItemResponse> subjectBookEvaluationByUser;

    private int mEditionId;


    public BookDetailPresenterImpl(UseCaseFactory useCaseFactory,
                                   SchedulerFactory schedulerFactory) {

        this.useCaseFactory = useCaseFactory;
        this.schedulerFactory = schedulerFactory;

        subjectBookInfo = PublishSubject.create();
        subjectBookEditionEvaluation = PublishSubject.create();
        subjectReadingStatus = PublishSubject.create();
        subjectEditionSharingUsers = PublishSubject.create();
        subjectBookEvaluationByUser = PublishSubject.create();
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDestroy() {

    }


    @Override
    public void setEditionId(int editionId) {
        this.mEditionId = editionId;
    }

    @Override
    public void getBookInfo() {
        MZDebug.i("_______________________________________ getBookInfo, editionId = " + mEditionId);

        useCaseGetBookInfo = useCaseFactory.getBookInfo(mEditionId);
        useCaseGetBookInfo.executeOn(schedulerFactory.io())
                .returnOn(schedulerFactory.main())
                .onNext(bookInfo -> {
                    subjectBookInfo.onNext(bookInfo);
                })
                .onError(throwable -> {
                    MZDebug.e("ERROR: getBookInfo __________________________________________ E \n\r"
                            + Log.getStackTraceString(throwable));
                })
                .execute();
    }

    @Override
    public Observable<BookInfo> onGetBookInfoSuccess() {
        return subjectBookInfo.subscribeOn(schedulerFactory.main());
    }

    @Override
    public void getSelfReadingStatus() {
        MZDebug.i("______________________________ getSelfReadingStatus, editionId = " + mEditionId);

        useCaseGetReadingStatus = useCaseFactory.getReadingStatus(mEditionId);
        useCaseGetReadingStatus.executeOn(schedulerFactory.io())
                .returnOn(schedulerFactory.main())
                .onNext(readingInfo -> {
                    subjectReadingStatus.onNext(readingInfo);
                })
                .onError(throwable -> {
                    MZDebug.e("ERROR: getSelfReadingStatus _________________________________ E \n\r"
                            + Log.getStackTraceString(throwable));
                })
                .execute();

    }

    @Override
    public Observable<BookReadingInfo> onGetSelfReadingStatusSuccess() {
        return subjectReadingStatus.subscribeOn(schedulerFactory.main());
    }

    @Override
    public void getEditionSharingUsers() {
        MZDebug.i("____________________________ getEditionSharingUsers, editionId = " + mEditionId);

        useCaseGetEditionSharingUsers = useCaseFactory.getEditionSharingUser(mEditionId);
        useCaseGetEditionSharingUsers.executeOn(schedulerFactory.io())
                .returnOn(schedulerFactory.main())
                .onNext(users -> {
                    subjectEditionSharingUsers.onNext(users);
                })
                .onError(throwable -> {
                    MZDebug.e("ERROR: getEditionSharingUsers _______________________________ E \n\r"
                            + Log.getStackTraceString(throwable));
                })
                .execute();
    }

    @Override
    public Observable<List<UserResponse>> onGetEditionSharingUsersSuccess() {
        return subjectEditionSharingUsers.subscribeOn(schedulerFactory.main());
    }

    @Override
    public void getBookEvaluationByUser() {
        MZDebug.i("___________________________ getBookEvaluationByUser, editionId = " + mEditionId);

        useCaseGetBookEvaluationByUser = useCaseFactory.getBookEvaluationByUser(mEditionId);
        useCaseGetBookEvaluationByUser.executeOn(schedulerFactory.io())
                .returnOn(schedulerFactory.main())
                .onNext(evaluation -> {
                    subjectBookEvaluationByUser.onNext(evaluation);
                })
                .onError(throwable -> {
                    MZDebug.e("ERROR: getBookEvaluationByUser _______________________________ E \n\r"
                            + Log.getStackTraceString(throwable));
                })
                .execute();
    }

    @Override
    public Observable<EvaluationItemResponse> onGetBookEvaluationByUser() {
        return subjectBookEvaluationByUser.subscribeOn(schedulerFactory.main());
    }


    @Override
    public void getBookEditionEvaluation() {
        MZDebug.i("__________________________ getBookEditionEvaluation, editionId = " + mEditionId);

        useCaseGetBookEditionEvaluation = useCaseFactory.getBookEditionEvaluation(mEditionId);
        useCaseGetBookEditionEvaluation.executeOn(schedulerFactory.io())
                .returnOn(schedulerFactory.main())
                .onNext(evaluations -> {
                    subjectBookEditionEvaluation.onNext(evaluations);
                })
                .onError(throwable -> {
                    MZDebug.e("ERROR: getBookEditionEvaluation _____________________________ E \n\r"
                            + Log.getStackTraceString(throwable));
                })
                .execute();
    }

    @Override
    public Observable<List<EvaluationItemResponse>> onGetBookEditionEvaluationSuccess() {
        return subjectBookEditionEvaluation.subscribeOn(schedulerFactory.main());
    }

    @Override
    public Observable<String> onError() {
        return null;
    }

    @Override
    public Observable<String> onUserNotLoggedIn() {
        return null;
    }
}
