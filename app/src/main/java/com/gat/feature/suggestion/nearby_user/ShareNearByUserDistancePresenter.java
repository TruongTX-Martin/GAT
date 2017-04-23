package com.gat.feature.suggestion.nearby_user;

import com.gat.repository.entity.UserNearByDistance;
import com.google.android.gms.maps.model.LatLng;
import com.rey.mvp2.Presenter;
import java.util.List;
import io.reactivex.Observable;

/**
 * Created by mozaa on 30/03/2017.
 */

public interface ShareNearByUserDistancePresenter extends Presenter {

    void requestUserNearOnTheMap(LatLng userLocation, LatLng neLocation, LatLng wsLocation);
    Observable<List<UserNearByDistance>> onPeopleNearByUserSuccess();

    Observable<String> onError();
}
