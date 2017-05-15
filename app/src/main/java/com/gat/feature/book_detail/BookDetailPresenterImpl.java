package com.gat.feature.book_detail;

import android.util.Log;
import com.gat.common.util.MZDebug;
import com.gat.data.response.ServerResponse;
import com.gat.data.response.UserResponse;
import com.gat.data.response.impl.BookInfo;
import com.gat.data.response.impl.BookReadingInfo;
import com.gat.data.response.impl.EvaluationItemResponse;
import com.gat.domain.SchedulerFactory;
import com.gat.domain.UseCaseFactory;
import com.gat.domain.usecase.UseCase;
import com.gat.feature.book_detail.self_update_reading.ReadingState;
import com.gat.repository.entity.User;
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
    private final Subject<String> subjectReadingStatusFailure;

    private UseCase<List<UserResponse>> useCaseGetEditionSharingUsers;
    private final Subject<List<UserResponse>> subjectEditionSharingUsers;

    private UseCase<EvaluationItemResponse> useCaseGetBookEvaluationByUser;
    private final Subject<EvaluationItemResponse> subjectBookEvaluationByUser;
    private final Subject<String> subjectEvaluationByUserFailure;

    private UseCase<ServerResponse> useCaseSelfUpdateReadingStatus;
    private final Subject<String> subjectSelfUpdateReadingStatus;
    private final Subject<String> subjectSelfUpdateReadingFailure;

    private final Subject<String> subjectOnError;

    private final Subject<User> subjectUserLoggedIn;
    private final Subject<String> subjectUserNotLoggedIn;

    private UseCase<ServerResponse> useCasePostRating;
    private final Subject<String> subjectPostRatingSuccess;

    private final Subject<Boolean> subjectCheckLoginDone;

    private int mEditionId;
    private boolean isLoggedIn;

    public BookDetailPresenterImpl(UseCaseFactory useCaseFactory,
                                   SchedulerFactory schedulerFactory) {

        this.useCaseFactory = useCaseFactory;
        this.schedulerFactory = schedulerFactory;

        subjectBookInfo = PublishSubject.create();
        subjectBookEditionEvaluation = PublishSubject.create();
        subjectReadingStatus = PublishSubject.create();
        subjectEditionSharingUsers = PublishSubject.create();
        subjectBookEvaluationByUser = PublishSubject.create();
        subjectOnError = PublishSubject.create();
        subjectSelfUpdateReadingStatus = PublishSubject.create();
        subjectEvaluationByUserFailure = PublishSubject.create();
        subjectReadingStatusFailure = PublishSubject.create();
        subjectSelfUpdateReadingFailure = PublishSubject.create();
        subjectUserLoggedIn = PublishSubject.create();
        subjectUserNotLoggedIn = PublishSubject.create();
        subjectPostRatingSuccess = PublishSubject.create();
        subjectCheckLoginDone = PublishSubject.create();
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
                    subjectOnError.onNext("Failed");
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
                    if (null == readingInfo) {
                        MZDebug.e("_____________________________ getSelfReadingStatus, editionId = "
                                + mEditionId + ", reading status NULL");
                        subjectReadingStatusFailure.onNext("Reading info = null");
                    } else {
                        MZDebug.e("_____________________________ getSelfReadingStatus, editionId = "
                                + mEditionId + ", reading status = " + readingInfo.getReadingStatus());
                        subjectReadingStatus.onNext(readingInfo);
                    }

                })
                .onError(throwable -> {
                    MZDebug.e("ERROR: getSelfReadingStatus _________________________________ E \n\r"
                            + Log.getStackTraceString(throwable));
                    subjectReadingStatusFailure.onNext("Reading info = null");
                })
                .execute();
    }

    @Override
    public Observable<BookReadingInfo> onGetSelfReadingStatusSuccess() {
        return subjectReadingStatus.subscribeOn(schedulerFactory.main());
    }

    @Override
    public Observable<String> onGetSelfReadingStatusFailure() {
        return subjectReadingStatusFailure.subscribeOn(schedulerFactory.main());
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
                    MZDebug.e("ERROR: getBookEvaluationByUser ______________________________ E \n\r"
                            + Log.getStackTraceString(throwable));
                    subjectEvaluationByUserFailure.onNext("Failed");
                })
                .execute();
    }

    @Override
    public Observable<EvaluationItemResponse> onGetBookEvaluationByUser() {
        return subjectBookEvaluationByUser.subscribeOn(schedulerFactory.main());
    }

    @Override
    public Observable<String> onGetEvaluationByUserFailure() {
        return subjectEvaluationByUserFailure.subscribeOn(schedulerFactory.main());
    }

    @Override
    public void updateReadingStatus() {
        MZDebug.w("________________________________ updateReadingStatus, editionId = "
                + mEditionId + " TO READ = 2 ");

        useCaseSelfUpdateReadingStatus = useCaseFactory.selfUpdateReadingStatus(mEditionId, ReadingState.TO_READ);
        useCaseSelfUpdateReadingStatus.executeOn(schedulerFactory.io()).
                returnOn(schedulerFactory.main()).
                onNext(serverResponse -> {
                    MZDebug.e("SUCCESS: updateReadingStatus : message = " + serverResponse.message());
                    subjectSelfUpdateReadingStatus.onNext(serverResponse.message());
                }).
                onError(throwable -> {
                    MZDebug.e("ERROR: updateReadingStatus ______________________________ E \n\r"
                            + Log.getStackTraceString(throwable));
                    subjectSelfUpdateReadingFailure.onNext("Failed");
                }).execute();
    }

    @Override
    public Observable<String> onUpdateReadingStatusSuccess() {
        return subjectSelfUpdateReadingStatus.subscribeOn(schedulerFactory.main());
    }

    @Override
    public Observable<String> onUpdateReadingStatusFailure() {
        return subjectSelfUpdateReadingFailure.subscribeOn(schedulerFactory.main());
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
        return subjectOnError.subscribeOn(schedulerFactory.main());
    }

    @Override
    public void isUserLoggedIn() {

        UseCase<User> userUseCase = useCaseFactory.getUser();
        userUseCase.executeOn(schedulerFactory.main())
                .returnOn(schedulerFactory.main())
                .onNext(user -> {
                    if (user != null) {
                        subjectUserLoggedIn.onNext(user);
                    } else {
                        subjectUserNotLoggedIn.onNext("Failed");
                    }

                })
                .onError(throwable -> {
                    MZDebug.e("ERROR: isUserLoggedIn ___________________________________ E \n\r"
                            + Log.getStackTraceString(throwable));
                    subjectUserNotLoggedIn.onNext("Failed");
                })
                .execute();
    }

    @Override
    public Observable<User> onUserLoggedIn() {
        return subjectUserLoggedIn.subscribeOn(schedulerFactory.main());
    }

    @Override
    public Observable<String> onUserNotLoggedIn() {
        return subjectUserNotLoggedIn.subscribeOn(schedulerFactory.main());
    }

    @Override
    public void postRating(int editionId, float rating, String review, boolean spoiler) {
        useCasePostRating = useCaseFactory.postComment(editionId, (int) rating, review, spoiler);
        useCasePostRating.executeOn(schedulerFactory.io())
                .returnOn(schedulerFactory.main())
                .onNext(serverResponse -> {
                    subjectPostRatingSuccess.onNext(serverResponse.message());
                })
                .onError(throwable -> {
                    MZDebug.e("ERROR: postComment __________________________________________ E \n\r"
                            + Log.getStackTraceString(throwable));
                    subjectOnError.onNext("Failed");
                }).execute();
    }

    @Override
    public Observable<String> onRatingSuccess() {
        return subjectPostRatingSuccess.subscribeOn(schedulerFactory.main());
    }

    @Override
    public void checkLogin() {
        useCaseFactory.getUser().executeOn(schedulerFactory.io())
                .returnOn(schedulerFactory.main())
                .onNext(user -> {
                    if (user == null || user.isValid()) {
                        isLoggedIn = false;
                        subjectCheckLoginDone.onNext(false);
                    } else {
                        isLoggedIn = true;
                        subjectCheckLoginDone.onNext(true);
                    }
                })
                .onError(throwable -> {
                    isLoggedIn = false;
                })
                .execute();
    }

    @Override
    public Observable<Boolean> onCheckLoginDone() {
        return subjectCheckLoginDone.subscribeOn(schedulerFactory.main());
    }
}
