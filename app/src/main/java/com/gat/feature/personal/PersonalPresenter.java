package com.gat.feature.personal;

import com.gat.data.response.ResponseData;
import com.gat.data.response.ServerResponse;
import com.gat.feature.personal.entity.BookInstanceInput;
import com.gat.repository.entity.Data;
import com.rey.mvp2.Presenter;

import io.reactivex.Observable;

/**
 * Created by truongtechno on 23/03/2017.
 */

public interface PersonalPresenter extends Presenter{

    Observable<Data> getResponsePersonal();
    Observable<ServerResponse<ResponseData>> onErrorPersonal();


    void requestBookInstance(BookInstanceInput input);
    Observable<Data> getResponseBookInstance();
    Observable<ServerResponse<ResponseData>> onErrorBookInstance();
}
