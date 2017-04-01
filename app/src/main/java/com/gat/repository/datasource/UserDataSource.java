package com.gat.repository.datasource;

import android.location.Address;

import com.gat.data.response.ResponseData;
import com.gat.data.response.ServerResponse;
import com.gat.data.response.impl.LoginResponseData;
import com.gat.data.response.impl.LoginResponseData;
import com.gat.data.response.impl.ResetPasswordResponseData;
import com.gat.data.response.impl.VerifyTokenResponseData;
import com.gat.repository.entity.LoginData;
import com.gat.repository.entity.User;
import com.gat.repository.entity.UserNearByDistance;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.internal.operators.observable.ObservableSerialized;

/**
 * Created by Rey on 2/23/2017.
 */

public interface UserDataSource {

    Observable<User> loadUser();
    Observable<User> persitUser(User user);
    Observable<User> getUserInformation();

    Observable<ServerResponse<LoginResponseData>> login(LoginData data);
    Observable<LoginData> loadLoginData();
    void saveLoginData(LoginData loginData);
    void storeLoginToken(String token);
    Observable<String> getLoginToken();

    Observable<ServerResponse<LoginResponseData>> register(LoginData data);


    Observable<ServerResponse<ResetPasswordResponseData>> sendRequestResetPassword(String email);

    Observable<ServerResponse<ResetPasswordResponseData>> storeResetToken(ServerResponse<ResetPasswordResponseData> data);

    Observable<ServerResponse<ResetPasswordResponseData>> getResetToken();

    Observable<ServerResponse<VerifyTokenResponseData>> verifyToken(String code, String tokenReset);
    Observable<ServerResponse<VerifyTokenResponseData>> storeVerifyToken(ServerResponse<VerifyTokenResponseData> data);
    Observable<ServerResponse<VerifyTokenResponseData>> getVerifyToken();
    Observable<ServerResponse<LoginResponseData>> changePassword(String password, String tokenVerified);


    Observable<List<Address>> getAddress(LatLng location);

    Observable<ServerResponse> updateLocation(String address, float longitude, float latitude);
    Observable<ServerResponse> updateCategories(List<Integer> categories);

    Observable<List<UserNearByDistance>> getPeopleNearByUserByDistance
            (float currentLongitude, float currentLatitude,
             float neLongitude, float neLatitude,
             float wsLongitude, float wsLatitude);



}
