package com.gat.feature.register.update.location;

import com.gat.data.response.ResponseData;
import com.gat.data.response.ServerResponse;
import com.gat.data.user.UserAddressData;
import com.google.android.gms.maps.model.LatLng;
import com.rey.mvp2.Presenter;

import io.reactivex.Observable;

/**
 * Created by ducbtsn on 3/19/17.
 */

public interface AddLocationPresenter extends Presenter {
    void setLocation(UserAddressData location);
    Observable<ServerResponse> updateResult();
    Observable<String> onError();

    void getLocationFromAdress(String address);
    Observable<LatLng> addressResult();
}
