package com.gat.feature.bookdetailsender;

import com.gat.data.response.ResponseData;
import com.gat.data.response.ServerResponse;
import com.gat.feature.personal.entity.RequestStatusInput;
import com.gat.repository.entity.Data;
import com.rey.mvp2.Presenter;
import io.reactivex.Observable;

/**
 * Created by root on 23/04/2017.
 */

public interface BookDetailSenderPresenter extends Presenter{

    void requestBookDetail(int input);
    Observable<Data> getResponseBookDetail();
    Observable<ServerResponse<ResponseData>> onErrorBookDetail();


    void requestSenderChangeStatus(RequestStatusInput input);
    Observable<Data> getResponseSenderChangeStatus();
    Observable<ServerResponse<ResponseData>> onErrorSenderChangeStatus();
}
