package com.gat.feature.personal;

import com.gat.data.response.ResponseData;
import com.gat.data.response.ServerResponse;
import com.gat.domain.usecase.UseCase;
import com.gat.repository.entity.Data;
import com.gat.repository.entity.User;
import com.gat.repository.entity.UserInfo;
import com.rey.mvp2.Presenter;

import io.reactivex.Observable;

/**
 * Created by truongtechno on 23/03/2017.
 */

public interface PersonalPresenter extends Presenter{

    Observable<Data> getResponse();
    Observable<ServerResponse<ResponseData>> onError();
}
