package com.gat.repository.impl;

import android.location.Address;

import com.gat.data.response.ServerResponse;
import com.gat.data.response.impl.ResetPasswordResponseData;
import com.gat.data.response.impl.VerifyTokenResponseData;
import com.gat.feature.personal.entity.BookInstanceInput;
import com.gat.repository.UserRepository;
import com.gat.repository.datasource.UserDataSource;
import com.gat.feature.personal.entity.Data;
import com.gat.repository.entity.LoginData;
import com.gat.repository.entity.User;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import dagger.Lazy;
import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

/**
 * Created by Rey on 2/23/2017.
 */

public class UserRepositoryImpl implements UserRepository {

    private final Lazy<UserDataSource> networkUserDataSourceLazy;
    private final Lazy<UserDataSource> localUserDataSourceLazy;

    private final Subject<User> userSubject;

    public UserRepositoryImpl(Lazy<UserDataSource> networkUserDataSourceLazy, Lazy<UserDataSource> localUserDataSourceLazy) {
        this.networkUserDataSourceLazy = networkUserDataSourceLazy;
        this.localUserDataSourceLazy = localUserDataSourceLazy;
        this.userSubject= PublishSubject.create();
    }

    @Override
    public Observable<User> getUser() {
        return Observable.defer(() -> localUserDataSourceLazy.get().loadUser()
                .concatWith(userSubject)
                .distinctUntilChanged());
    }

    @Override
    public Observable<User> login(LoginData data) {
        return Observable.defer(() -> networkUserDataSourceLazy.get().login(data)
                .flatMap(response -> {
                    localUserDataSourceLazy.get().storeLoginToken(response.data().loginToken());
                    localUserDataSourceLazy.get().saveLoginData(data);
                    return networkUserDataSourceLazy.get().getUserInformation();
                })
                .flatMap(user -> localUserDataSourceLazy.get().persitUser(user))
                .doOnNext(userSubject::onNext));
    }

    @Override
    public Observable<User> login() {
        return Observable.defer(() -> localUserDataSourceLazy.get().loadLoginData()
                .flatMap(loginData -> networkUserDataSourceLazy.get().login(loginData))
                .flatMap(response -> networkUserDataSourceLazy.get().getUserInformation())
                .doOnNext(userSubject::onNext));
    }

    @Override
    public Observable<LoginData> getLoginData() {
        return Observable.defer(() -> localUserDataSourceLazy.get().loadLoginData());
    }


    @Override
    public Observable<User> register(LoginData data) {
        return Observable.defer(() -> networkUserDataSourceLazy.get().register(data)
                .flatMap(response -> {
                    localUserDataSourceLazy.get().saveLoginData(data);
                    localUserDataSourceLazy.get().storeLoginToken(response.data().loginToken());
                    return networkUserDataSourceLazy.get().getUserInformation();
                })
                .flatMap(user -> localUserDataSourceLazy.get().persitUser(user))
                .doOnNext(userSubject::onNext)
        );
    }

    @Override
    public Observable<ServerResponse> sendRequestResetPassword(String email) {
        return Observable.defer(() -> networkUserDataSourceLazy.get().sendRequestResetPassword(email)
                .flatMap(response -> localUserDataSourceLazy.get().storeResetToken(response))
        );
    }

    @Override
    public Observable<ServerResponse> verifyToken(String code) {
        Observable<ServerResponse<ResetPasswordResponseData>> storeResetData = localUserDataSourceLazy.get().getResetToken();
        return Observable.defer(() -> networkUserDataSourceLazy.get()
                .verifyToken(code, storeResetData.blockingFirst().data().tokenResetPassword())
                .flatMap(response -> localUserDataSourceLazy.get().storeVerifyToken(response)));
    }

    @Override
    public Observable<ServerResponse> changePassword(String password) {
        Observable<ServerResponse<VerifyTokenResponseData>> storeVerifyData = localUserDataSourceLazy.get().getVerifyToken();
        return Observable.defer(() -> networkUserDataSourceLazy.get()
                .changePassword(password, storeVerifyData.blockingFirst().data().tokenVerify())
                .map(response -> {
                    localUserDataSourceLazy.get().storeLoginToken(response.data().loginToken());
                    return response;
                }));
    }

    @Override
    public Observable<List<Address>> getAddress(LatLng location) {
        return Observable.defer(() -> networkUserDataSourceLazy.get().getAddress(location));
    }

    @Override
    public Observable<ServerResponse> updateLocation(String address, LatLng location) {
        return Observable.defer(() -> networkUserDataSourceLazy.get().updateLocation(address, (float)location.longitude, (float)location.latitude));
    }

    @Override
    public Observable<ServerResponse> updateCategories(List<Integer> categories) {
        return Observable.defer(() -> networkUserDataSourceLazy.get().updateCategories(categories));
    }

    @Override
    public Observable<Data> getPersonalData() {
        System.out.println("UserRepositoryImpl");
        Observable<Data>  stringObservable = Observable.defer(() -> networkUserDataSourceLazy.get().getPersonalInfo());
        //convert
        return  stringObservable;

    }

    @Override
    public Observable<Data> getBookInstance(BookInstanceInput input) {
        return Observable.defer( () -> networkUserDataSourceLazy.get().getBookInstance(input));
    }

}
