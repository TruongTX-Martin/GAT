package com.gat.feature.book_detail;

import com.gat.data.response.UserResponse;
import com.gat.data.response.impl.BookInfo;
import com.gat.data.response.impl.BookReadingInfo;
import com.gat.data.response.impl.EvaluationItemResponse;
import com.rey.mvp2.Presenter;
import java.util.List;
import io.reactivex.Observable;

/**
 * Created by mryit on 4/16/2017.
 */

public interface BookDetailPresenter extends Presenter {

    void setEditionId (int editionId);

    void getBookInfo ();
    Observable<BookInfo> onGetBookInfoSuccess ();

    void getBookEditionEvaluation();
    Observable<List<EvaluationItemResponse>> onGetBookEditionEvaluationSuccess ();

    void getSelfReadingStatus ();
    Observable<BookReadingInfo> onGetSelfReadingStatusSuccess ();
    Observable<String> onGetSelfReadingStatusFailure ();

    void getEditionSharingUsers ();
    Observable<List<UserResponse>> onGetEditionSharingUsersSuccess ();

    void getBookEvaluationByUser ();
    Observable<EvaluationItemResponse> onGetBookEvaluationByUser ();
    Observable<String> onGetEvaluationByUserFailure ();

    void updateReadingStatus ();
    Observable<String> onUpdateReadingStatusSuccess ();
    Observable<String> onUpdateReadingStatusFailure ();

    Observable<String> onError ();

    void isUserLoggedIn ();
    Observable<String> onUserLoggedIn ();
    Observable<String> onUserNotLoggedIn ();

}
