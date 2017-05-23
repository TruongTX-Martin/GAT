package com.gat.feature.book_detail.comment;

import android.util.Log;

import com.gat.common.util.MZDebug;
import com.gat.data.exception.CommonException;
import com.gat.data.exception.LoginException;
import com.gat.data.response.ServerResponse;
import com.gat.domain.SchedulerFactory;
import com.gat.domain.UseCaseFactory;
import com.gat.domain.usecase.UseCase;
import com.gat.repository.BookRepository;
import com.gat.repository.UserRepository;
import com.gat.repository.entity.User;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

/**
 * Created by mryit on 4/23/2017.
 */

public class CommentPresenterImpl implements CommentPresenter{

    private final UseCaseFactory useCaseFactory;
    private final SchedulerFactory schedulerFactory;

    private UseCase<ServerResponse> useCasePostComment;
    private final Subject<String> subjectPostCommentSuccess;
    private final Subject<String> subjectPostCommentFailure;

    private UseCase<User> useCaseLoadUser;
    private final Subject<User> subjectLoadUserSuccess;

    private final Subject<String> subjectUnAuthorization;

    public CommentPresenterImpl(UseCaseFactory useCaseFactory, SchedulerFactory schedulerFactory) {
        this.useCaseFactory = useCaseFactory;
        this.schedulerFactory = schedulerFactory;
        subjectPostCommentSuccess = PublishSubject.create();
        subjectPostCommentFailure = PublishSubject.create();
        subjectLoadUserSuccess = PublishSubject.create();
        subjectUnAuthorization = PublishSubject.create();
    }


    @Override
    public void onCreate() {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public void postComment(int editionId, float rating, String review, boolean spoiler, Integer evaluationId, Integer readingId, int bookId) {
        useCasePostComment = useCaseFactory.postComment(editionId, (int) rating, review, spoiler, evaluationId, readingId, bookId);
        useCasePostComment.executeOn(schedulerFactory.io())
                .returnOn(schedulerFactory.main())
                .onNext(serverResponse -> {
                    subjectPostCommentSuccess.onNext(serverResponse.message());
                })
                .onError(throwable -> {
                    MZDebug.e("ERROR: postComment __________________________________________ E \n\r"
                            + Log.getStackTraceString(throwable));

                    if (throwable instanceof LoginException) {
                        LoginException exception = (LoginException) throwable;
                        subjectUnAuthorization.onNext(exception.responseData().message());
                    } else if (throwable instanceof CommonException) {
                        subjectPostCommentFailure.onNext(((CommonException) throwable).getMessage());
                    } else {
                        subjectPostCommentFailure.onNext(ServerResponse.EXCEPTION.message());
                    }

                }).execute();

    }

    @Override
    public Observable<String> onPostCommentSuccess() {
        return subjectPostCommentSuccess.subscribeOn(schedulerFactory.main());
    }

    @Override
    public Observable<String> onPostCommentFailure() {
        return subjectPostCommentFailure.subscribeOn(schedulerFactory.main());
    }

    @Override
    public void loadUserCached() {
        useCaseLoadUser = useCaseFactory.getUser();
        useCaseLoadUser.executeOn(schedulerFactory.io())
                .returnOn(schedulerFactory.main())
                .onNext(user -> {
                    subjectLoadUserSuccess.onNext(user);
                })
                .onError(throwable -> {
                    MZDebug.e("ERROR: CommentPresenterImpl loadUserCached __________________ E \n\r"
                            + Log.getStackTraceString(throwable));
                })
                .execute();
    }

    @Override
    public Observable<User> onLoadUserCachedSuccess() {
        return subjectLoadUserSuccess.subscribeOn(schedulerFactory.main());
    }

    @Override
    public Observable<String> onUnAuthorization() {
        return subjectUnAuthorization.subscribeOn(schedulerFactory.main());
    }
}
