package com.gat.feature.bookdetailowner;

import com.gat.data.response.ResponseData;
import com.gat.data.response.ServerResponse;
import com.gat.feature.bookdetailsender.entity.ChangeStatusResponse;
import com.gat.feature.personal.entity.RequestStatusInput;
import com.gat.repository.entity.Data;
import com.rey.mvp2.Presenter;

import io.reactivex.Observable;

/**
 * Created by root on 26/04/2017.
 */

public interface BookDetailOwnerPresenter extends Presenter{

    void requestBookDetail(int input);
    Observable<Data> getResponseBookDetail();
    Observable<ServerResponse<ResponseData>> onErrorBookDetail();

    void requestChangeStatus(RequestStatusInput input);
    Observable<ChangeStatusResponse> getResponseChangeStatus();
    Observable<ServerResponse<ResponseData>> onErrorChangeStatus();
}
