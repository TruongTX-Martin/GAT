package com.gat.feature.suggestion.nearby_user;

import com.gat.domain.SchedulerFactory;
import com.gat.domain.UseCaseFactory;
import com.gat.repository.entity.UserNearByDistance;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import io.reactivex.Observable;

/**
 * Created by mozaa on 30/03/2017.
 */

public class ShareNearByUserDistancePresenterImpl implements ShareNearByUserDistancePresenter {

    public ShareNearByUserDistancePresenterImpl(UseCaseFactory useCaseFactory, SchedulerFactory schedulerFactory) {

    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public void requetUserNearOnTheMap(LatLng userLocation, LatLng neLocation, LatLng wsLocation) {

    }

    @Override
    public Observable<List<UserNearByDistance>> onPeopleNearByUserSuccess() {
        return null;
    }

    @Override
    public Observable<String> onError() {
        return null;
    }
}
