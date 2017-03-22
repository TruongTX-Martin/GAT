package com.gat.repository;

import android.location.Address;

import com.gat.data.response.ServerResponse;
import com.gat.data.response.impl.LoginResponseData;
import com.gat.data.response.impl.LoginResponseData;
import com.gat.data.response.impl.ResetPasswordResponseData;
import com.gat.data.response.impl.VerifyTokenResponseData;
import com.gat.repository.entity.LoginData;
import com.gat.repository.entity.User;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import io.reactivex.Observable;

/**
 * Created by Rey on 2/23/2017.
 */

public interface UserRepository {

    Observable<User> getUser();

    Observable<User> login(LoginData data);

    Observable<User> login();

    Observable<LoginData> getLoginData();

    Observable<User> register(LoginData data);

    Observable<ServerResponse> sendRequestResetPassword(String email);

    Observable<ServerResponse> verifyToken(String code);

    Observable<ServerResponse> changePassword(String password);

    Observable<List<Address>> getAddress(LatLng location);

    Observable<ServerResponse> updateLocation(String address, LatLng location);
    Observable<ServerResponse> updateCategories(List<Integer> categories);
}
