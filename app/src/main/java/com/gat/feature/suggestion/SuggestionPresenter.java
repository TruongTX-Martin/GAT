package com.gat.feature.suggestion;

import com.gat.data.response.BookResponse;
import com.gat.repository.entity.UserNearByDistance;
import com.google.android.gms.maps.model.LatLng;
import com.rey.mvp2.Presenter;

import java.util.List;

import io.reactivex.Observable;

/**
 * Created by ducbtsn on 3/25/17.
 */

public interface SuggestionPresenter extends Presenter{

    void getPeopleNearByUser(LatLng userLocation, LatLng neLocation, LatLng wsLocation);
    Observable<List<UserNearByDistance>> onPeopleNearByUserSuccess();

    void suggestMostBorrowing();
    Observable<List<BookResponse>> onTopBorrowingSuccess();

    void suggestBooks();
    Observable<List<BookResponse>> onBookSuggestSuccess();

    Observable<String> onError();

    void getUnReadGroupMessage();
    Observable<Integer> unReadCnt();
}
