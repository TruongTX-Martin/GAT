package com.gat.feature.suggestion;

import android.util.Log;

import com.gat.common.util.MZDebug;
import com.gat.data.response.BookResponse;
import com.gat.data.response.DataResultListResponse;
import com.gat.domain.SchedulerFactory;
import com.gat.domain.UseCaseFactory;
import com.gat.domain.UseCases;
import com.gat.domain.usecase.UseCase;

import java.util.List;

import com.gat.repository.entity.LoginData;
import com.gat.repository.entity.User;
import com.gat.repository.entity.UserNearByDistance;
import com.google.android.gms.maps.model.LatLng;
import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

/**
 * Created by ducbtsn on 3/25/17.
 */

public class SuggestionPresenterImpl implements SuggestionPresenter {

    private final UseCaseFactory useCaseFactory;
    private final SchedulerFactory schedulerFactory;

    private UseCase<List<BookResponse>> useCaseMostBorrowing;
    private final Subject<List<BookResponse>> resultMostBorrowingSubject;

    private UseCase<List<BookResponse>> useCaseBookSuggest;
    private final Subject<List<BookResponse>> resultBooksSuggestSubject;

    private UseCase<DataResultListResponse<UserNearByDistance>> userNearByDistance;
    private final Subject<DataResultListResponse<UserNearByDistance>> resultListUserNearByDistance;

    private UseCase<Integer> unReadGroupMessageUseCase;
    private final Subject<Integer> unReadGroupMessageSubject;

    private final Subject<String> errorSubject;

    private int mPage;
    private static int SIZE_OF_PAGE;


    public SuggestionPresenterImpl(UseCaseFactory useCaseFactory, SchedulerFactory schedulerFactory) {
        this.useCaseFactory = useCaseFactory;
        this.schedulerFactory = schedulerFactory;

        resultMostBorrowingSubject = PublishSubject.create();
        errorSubject = PublishSubject.create();
        resultListUserNearByDistance = PublishSubject.create();
        resultBooksSuggestSubject = PublishSubject.create();

        unReadGroupMessageSubject = BehaviorSubject.create();
    }

    @Override
    public void onCreate() {
        mPage = 1;
        SIZE_OF_PAGE = 5;
    }

    @Override
    public void onDestroy() {}


    @Override
    public void suggestMostBorrowing() {
        useCaseMostBorrowing = useCaseFactory.suggestMostBorrowing();
        useCaseMostBorrowing.executeOn(schedulerFactory.io())
                .returnOn(schedulerFactory.main())
                .onNext(listBook -> {
                    resultMostBorrowingSubject.onNext(listBook);
                })
                .onError(throwable -> {
                    MZDebug.e("ERROR: suggestMostBorrowing : _______________________________ E \n\r"
                            + Log.getStackTraceString(throwable));
                })
                .execute();
    }

    @Override
    public Observable<List<BookResponse>> onTopBorrowingSuccess() {
        return resultMostBorrowingSubject.subscribeOn(schedulerFactory.main());
    }

    @Override
    public void suggestBooks() {

        // get cached user data
        UseCase<User> loadLocalUser = useCaseFactory.getUser();
        loadLocalUser.executeOn(schedulerFactory.io())
                .returnOn(schedulerFactory.main())
                .onNext(user -> {
                    MZDebug.w("local login: " + user.isValid());
                    if (user == null) {
                        doSuggestBookWithoutLogin();
                    } else {
                        doSuggestBookAfterLogin();
                    }
                })
                .onError(throwable -> {
                    MZDebug.e("ERROR: suggestBooks : get local login data___________________ E \n\r"
                            + Log.getStackTraceString(throwable));
                })
                .execute();
    }

    private void doSuggestBookAfterLogin () {
        MZDebug.i("______________________________________________________doSuggestBookAfterLogin ");
        useCaseBookSuggest = useCaseFactory.suggestBooksAfterLogin();
        useCaseBookSuggest.executeOn(schedulerFactory.io())
                .returnOn(schedulerFactory.main())
                .onNext(listBook -> {
                    resultBooksSuggestSubject.onNext(listBook);
                })
                .onError(throwable -> {
                    MZDebug.e("ERROR: doSuggestBookAfterLogin ______________________________ E \n\r"
                            + Log.getStackTraceString(throwable));
                })
                .execute();
    }

    private void doSuggestBookWithoutLogin() {
        MZDebug.i("___________________________________________________ doSuggestBookWithoutLogin ");
        useCaseBookSuggest = useCaseFactory.suggestBooks();
        useCaseBookSuggest.executeOn(schedulerFactory.io())
                .returnOn(schedulerFactory.main())
                .onNext(listBook -> {
                    resultBooksSuggestSubject.onNext(listBook);
                })
                .onError(throwable -> {
                    MZDebug.e("ERROR: doSuggestBookWithoutLogin ____________________________ E \n\r"
                            + Log.getStackTraceString(throwable));
                })
                .execute();
    }


    @Override
    public Observable<List<BookResponse>> onBookSuggestSuccess() {
        return resultBooksSuggestSubject.subscribeOn(schedulerFactory.main());
    }


    @Override
    public void getPeopleNearByUser(LatLng userLocation, LatLng neLocation, LatLng wsLocation) {
        userNearByDistance = useCaseFactory.peopleNearByUser(userLocation, neLocation, wsLocation, mPage, SIZE_OF_PAGE);
        userNearByDistance.executeOn(schedulerFactory.io())
                .returnOn(schedulerFactory.main())
                .onNext(listUser-> {
                    resultListUserNearByDistance.onNext(listUser);
                })
                .onError(throwable -> {
                    MZDebug.e("ERROR: getPeopleNearByUser __________________________________ E \n\r"
                            + Log.getStackTraceString(throwable));
//                    errorSubject.onNext(Log.getStackTraceString(throwable));
                }).execute();
    }

    @Override
    public Observable<DataResultListResponse<UserNearByDistance>> onPeopleNearByUserSuccess() {
        return resultListUserNearByDistance.subscribeOn(schedulerFactory.main());
    }

    @Override
    public Observable<String> onError() {
        return errorSubject.subscribeOn(schedulerFactory.main());
    }

    @Override
    public void getUnReadGroupMessage() {
        unReadGroupMessageUseCase = UseCases.release(unReadGroupMessageUseCase);
        unReadGroupMessageUseCase = useCaseFactory.getUnReadGroupMessageCnt();
        unReadGroupMessageUseCase.executeOn(schedulerFactory.io())
                .returnOn(schedulerFactory.main())
                .onNext(cnt -> unReadGroupMessageSubject.onNext(cnt))
                .onError(throwable -> {
                    throwable.printStackTrace();
                    unReadGroupMessageSubject.onNext(0);
                })
                .onStop(() -> unReadGroupMessageUseCase = UseCases.release(unReadGroupMessageUseCase))
                .execute();
    }

    @Override
    public Observable<Integer> unReadCnt() {
        return unReadGroupMessageSubject.subscribeOn(schedulerFactory.main());
    }

}
