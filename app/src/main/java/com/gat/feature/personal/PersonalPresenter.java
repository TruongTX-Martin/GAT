package com.gat.feature.personal;

import com.gat.data.response.ResponseData;
import com.gat.data.response.ServerResponse;
import com.gat.feature.personal.entity.BookInstanceInput;
import com.gat.feature.personal.entity.BookChangeStatusInput;
import com.gat.feature.personal.entity.BookReadingInput;
import com.gat.feature.personal.entity.BookRequestInput;
import com.gat.repository.entity.Data;
import com.rey.mvp2.Presenter;

import io.reactivex.Observable;

/**
 * Created by truongtechno on 23/03/2017.
 */

public interface PersonalPresenter extends Presenter{

    void requestPersonalInfor(String input);
    Observable<Data> getResponsePersonal();
    Observable<String> onErrorPersonal();


    void requestBookInstance(BookInstanceInput input);
    Observable<Data> getResponseBookInstance();
    Observable<String> onErrorBookInstance();

    void requestChangeBookSharingStatus(BookChangeStatusInput input);
    Observable<Data> getResponseBookSharingStatus();
    Observable<String> onErrorBookSharingStatus();

    void requestReadingBooks(BookReadingInput input);
    Observable<Data> getResponseReadingBooks();
    Observable<String> onErrorReadingBooks();

    void requestBookRequests(BookRequestInput input);
    Observable<Data> getResponseBookRequest();
    Observable<String> onErrorBookRequest();
}
