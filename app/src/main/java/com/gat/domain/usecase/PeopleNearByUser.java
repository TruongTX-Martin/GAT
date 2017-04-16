package com.gat.domain.usecase;

import com.gat.data.response.ServerResponse;
import com.gat.repository.UserRepository;
import com.gat.repository.entity.UserNearByDistance;
import com.google.android.gms.maps.model.LatLng;
import io.reactivex.Observable;

import java.util.List;

/**
 * Created by mozaa on 01/04/2017.
 */
public class PeopleNearByUser extends  UseCase<List<UserNearByDistance>> {

    private final UserRepository repository;
    private final LatLng currentLocation;
    private final LatLng neLocation;
    private final LatLng wsLocation;
    private final int page;
    private final int sizeOfPage;

    public PeopleNearByUser(UserRepository repository, LatLng currentLocation, LatLng neLocation, LatLng wsLocation, int page, int sizeOfPage) {
        this.repository = repository;
        this.currentLocation = currentLocation;
        this.neLocation = neLocation;
        this.wsLocation = wsLocation;
        this.page = page;
        this.sizeOfPage = sizeOfPage;
    }


    @Override
    protected Observable<List<UserNearByDistance>>createObservable() {
        return repository.getPeopleNearByUser(currentLocation, neLocation, wsLocation, page, sizeOfPage);
    }

}
