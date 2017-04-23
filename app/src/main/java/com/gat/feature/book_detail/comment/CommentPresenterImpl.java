package com.gat.feature.book_detail.comment;

import android.util.Log;

import com.gat.common.util.MZDebug;
import com.gat.data.response.ServerResponse;
import com.gat.domain.SchedulerFactory;
import com.gat.domain.UseCaseFactory;
import com.gat.domain.usecase.UseCase;
import com.gat.repository.BookRepository;
import com.gat.repository.UserRepository;

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

    public CommentPresenterImpl(UseCaseFactory useCaseFactory, SchedulerFactory schedulerFactory) {
        this.useCaseFactory = useCaseFactory;
        this.schedulerFactory = schedulerFactory;
        subjectPostCommentSuccess = PublishSubject.create();
        subjectPostCommentFailure = PublishSubject.create();
    }


    @Override
    public void onCreate() {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public void postComment(int editionId, float rating, String review, boolean spoiler) {
        useCasePostComment = useCaseFactory.postComment(editionId, (int) rating, review, spoiler);
        useCasePostComment.executeOn(schedulerFactory.io())
                .returnOn(schedulerFactory.main())
                .onNext(serverResponse -> {
                    subjectPostCommentSuccess.onNext(serverResponse.message());
                })
                .onError(throwable -> {
                    MZDebug.e("ERROR: postComment __________________________________________ E \n\r"
                            + Log.getStackTraceString(throwable));
                    subjectPostCommentFailure.onNext("Failed");
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
}
