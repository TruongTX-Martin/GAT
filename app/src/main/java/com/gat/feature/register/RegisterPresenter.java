package com.gat.feature.register;

import com.gat.data.response.ResponseData;
import com.gat.data.response.ServerResponse;
import com.gat.data.user.UserAddressData;
import com.gat.repository.entity.LoginData;
import com.gat.repository.entity.User;
import com.rey.mvp2.Presenter;

import java.util.List;

import io.reactivex.Observable;

/**
 * Created by ducbtsn on 2/26/17.
 */

public interface RegisterPresenter extends Presenter {
    Observable<User> getResponse();
    void setIdentity(LoginData loginData);
    Observable<ServerResponse<ResponseData>> onError();
}
