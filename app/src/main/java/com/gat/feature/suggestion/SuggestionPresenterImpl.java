package com.gat.feature.suggestion;

import android.util.Log;

import com.gat.common.util.MZDebug;
import com.gat.data.response.BookResponse;
import com.gat.domain.SchedulerFactory;
import com.gat.domain.UseCaseFactory;
import com.gat.domain.usecase.UseCase;

import java.util.List;

import com.gat.repository.entity.UserNearByDistance;
import com.google.android.gms.maps.model.LatLng;
import io.reactivex.Observable;
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

    private UseCase<List<UserNearByDistance>> userNearByDistance;
    private final Subject<List<UserNearByDistance>> resultListUserNearByDistance;

    private final Subject<String> errorSubject;


    public SuggestionPresenterImpl(UseCaseFactory useCaseFactory, SchedulerFactory schedulerFactory) {
        this.useCaseFactory = useCaseFactory;
        this.schedulerFactory = schedulerFactory;

        resultMostBorrowingSubject = PublishSubject.create();
        errorSubject = PublishSubject.create();
        resultListUserNearByDistance = PublishSubject.create();
        resultBooksSuggestSubject = PublishSubject.create();
    }

    @Override
    public void onCreate() {}

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
//                    errorSubject.onNext(Log.getStackTraceString(throwable));
                })
                .execute();
    }

    @Override
    public Observable<List<BookResponse>> onTopBorrowingSuccess() {
        return resultMostBorrowingSubject.subscribeOn(schedulerFactory.main());
    }

    @Override
    public void suggestBooks() {
        useCaseBookSuggest = useCaseFactory.suggestBooks();
        useCaseBookSuggest.executeOn(schedulerFactory.io())
                .returnOn(schedulerFactory.main())
                .onNext(listBook -> {
                    resultBooksSuggestSubject.onNext(listBook);
                })
                .onError(throwable -> {
                    MZDebug.e("_______________________requestUserNearOnTheMap____onError_________" + throwable.getCause().getMessage());
//                    errorSubject.onNext(Log.getStackTraceString(throwable));
                })
                .execute();
    }

    @Override
    public Observable<List<BookResponse>> onBookSuggestSuccess() {
        return resultBooksSuggestSubject.subscribeOn(schedulerFactory.main());
    }


    @Override
    public void getPeopleNearByUser(LatLng userLocation, LatLng neLocation, LatLng wsLocation) {
        userNearByDistance = useCaseFactory.peopleNearByUser(userLocation, neLocation, wsLocation);
        userNearByDistance.executeOn(schedulerFactory.io())
                .returnOn(schedulerFactory.main())
                .onNext(listUser-> {
                    resultListUserNearByDistance.onNext(listUser);
                })
                .onError(throwable -> {

                    MZDebug.e("_______________________requestUserNearOnTheMap____onError_________" + throwable.getCause().getMessage());
//                    errorSubject.onNext(Log.getStackTraceString(throwable));
                }).execute();
    }

    @Override
    public Observable<List<UserNearByDistance>> onPeopleNearByUserSuccess() {
        return resultListUserNearByDistance.subscribeOn(schedulerFactory.main());
    }

    @Override
    public Observable<String> onError() {
        return errorSubject.subscribeOn(schedulerFactory.main());
    }

}
