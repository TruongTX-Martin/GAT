package com.gat.domain.usecase;

import android.util.Log;

import com.gat.data.response.ServerResponse;
import com.gat.repository.UserRepository;
import com.google.android.gms.maps.model.LatLng;

import io.reactivex.Observable;

/**
 * Created by ducbtsn on 3/5/17.
 */

public class UpdateLocation extends UseCase<ServerResponse> {
    private final UserRepository repository;
    private final String address;
    private final LatLng location;

    public UpdateLocation(UserRepository repository, String address, LatLng location) {
        this.repository = repository;
        this.address = address;
        this.location = location;
    }
    @Override
    protected Observable<ServerResponse> createObservable() {
        Log.d("UpdateLocation", "");
        return repository.updateLocation(address, location);
    }
}
