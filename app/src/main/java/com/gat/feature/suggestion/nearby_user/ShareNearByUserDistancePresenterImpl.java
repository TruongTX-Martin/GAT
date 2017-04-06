package com.gat.feature.suggestion.nearby_user;

import com.gat.common.util.MZDebug;
import com.gat.domain.SchedulerFactory;
import com.gat.domain.UseCaseFactory;
import com.gat.domain.usecase.UseCase;
import com.gat.repository.entity.UserNearByDistance;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

/**
 * Created by mozaa on 30/03/2017.
 */

public class ShareNearByUserDistancePresenterImpl implements ShareNearByUserDistancePresenter {


    private final UseCaseFactory useCaseFactory;
    private final SchedulerFactory schedulerFactory;

    UseCase<List<UserNearByDistance>> useCaseUserNear;
    Subject<List<UserNearByDistance>> resultUserNearSubject;

    private final Subject<String> errorSubject;


    public ShareNearByUserDistancePresenterImpl (
            UseCaseFactory useCaseFactory, SchedulerFactory schedulerFactory) {

        this.useCaseFactory = useCaseFactory;
        this.schedulerFactory = schedulerFactory;

        resultUserNearSubject = PublishSubject.create();
        errorSubject = PublishSubject.create();
    }

    @Override
    public void onCreate() {}

    @Override
    public void onDestroy() {}

    @Override
    public void requestUserNearOnTheMap(LatLng userLocation, LatLng neLocation, LatLng wsLocation) {
        useCaseUserNear = useCaseFactory.peopleNearByUser(userLocation, neLocation, wsLocation);
        useCaseUserNear.executeOn(schedulerFactory.io())
                .returnOn(schedulerFactory.main())
                .onNext(listUser -> {
                    resultUserNearSubject.onNext(listUser);
                })
                .onError(throwable -> {
                    MZDebug.e("_______________________requestUserNearOnTheMap____onError_________");
                }).execute();
    }

    @Override
    public Observable<List<UserNearByDistance>> onPeopleNearByUserSuccess() {
        return resultUserNearSubject.subscribeOn(schedulerFactory.main());
    }

    @Override
    public Observable<String> onError() {
        return errorSubject.subscribeOn(schedulerFactory.main());
    }
}
