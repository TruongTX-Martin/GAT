package com.gat.feature.book_detail.comment;

import com.gat.repository.entity.User;
import com.rey.mvp2.Presenter;

import io.reactivex.Observable;

/**
 * Created by mryit on 4/23/2017.
 */

public interface CommentPresenter extends Presenter {

    void postComment (int editionId, float rating, String review, boolean spoiler, Integer evaluationId, Integer readingId, int bookId);
    Observable<String> onPostCommentSuccess ();
    Observable<String> onPostCommentFailure ();

    void loadUserCached ();
    Observable<User> onLoadUserCachedSuccess ();

    Observable<String> onUnAuthorization ();
}
