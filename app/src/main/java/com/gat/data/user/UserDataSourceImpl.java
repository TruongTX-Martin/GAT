package com.gat.data.user;

import android.location.Address;

import com.gat.data.response.ServerResponse;
import com.gat.data.response.impl.LoginResponseData;
import com.gat.data.response.impl.ResetPasswordResponseData;
import com.gat.data.response.impl.VerifyTokenResponseData;
import com.gat.feature.personal.entity.BookChangeStatusInput;
import com.gat.feature.personal.entity.BookInstanceInput;
import com.gat.feature.personal.entity.BookReadingInput;
import com.gat.repository.datasource.UserDataSource;
import com.gat.repository.entity.Data;
import com.gat.repository.entity.LoginData;
import com.gat.repository.entity.User;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import io.reactivex.Observable;

/**
 * Created by ducbtsn on 3/4/17.
 */

public class UserDataSourceImpl implements UserDataSource {
    @Override
    public Observable<User> loadUser() {
        return null;
    }

    @Override
    public Observable<User> persitUser(User user) {
        return null;
    }

    @Override
    public Observable<User> getUserInformation() {
        return null;
    }

    @Override
    public Observable<ServerResponse<LoginResponseData>> login(LoginData data) {
        return null;
    }

    @Override
    public Observable<LoginData> loadLoginData() {
        return null;
    }

    @Override
    public void saveLoginData(LoginData loginData) {
        return;
    }

    @Override
    public Observable<ServerResponse<LoginResponseData>> register(LoginData data) {
        return null;
    }

    @Override
    public Observable<ServerResponse<ResetPasswordResponseData>> sendRequestResetPassword(String email) {
        return null;
    }

    @Override
    public Observable<ServerResponse<ResetPasswordResponseData>> storeResetToken(ServerResponse<ResetPasswordResponseData> data) {
        return null;
    }

    @Override
    public Observable<ServerResponse<ResetPasswordResponseData>> getResetToken() {
        return null;
    }

    @Override
    public Observable<ServerResponse<VerifyTokenResponseData>> verifyToken(String code, String tokenReset) {
        return null;
    }

    @Override
    public Observable<ServerResponse<VerifyTokenResponseData>> storeVerifyToken(ServerResponse<VerifyTokenResponseData> data) {
        return null;
    }

    @Override
    public Observable<ServerResponse<VerifyTokenResponseData>> getVerifyToken() {
        return null;
    }

    @Override
    public Observable<ServerResponse<LoginResponseData>> changePassword(String password, String tokenVerified) {
        return null;
    }

    @Override
    public void storeLoginToken(String token) {
        return;
    }

    @Override
    public Observable<String> getLoginToken() {
        return null;
    }

    @Override
    public Observable<List<Address>> getAddress(LatLng location) {
        return null;
    }

    @Override
    public Observable<ServerResponse> updateLocation(String address, float longitude, float latitude) {
        return null;
    }

    @Override
    public Observable<ServerResponse> updateCategories(List<Integer> categories) {
        return null;
    }

    @Override
    public Observable<Data> getPersonalInfo() {
        return null;
    }

    @Override
    public Observable<Data> getBookInstance(BookInstanceInput input) {
        return null;
    }

    @Override
    public Observable<Data> changeBookSharingStatus(BookChangeStatusInput input) {
        return null;
    }

    @Override
    public Observable<Data> getReadingBook(BookReadingInput input) {
        return null;
    }
}
