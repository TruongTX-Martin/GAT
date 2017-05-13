package com.gat.repository.impl;

import android.location.Address;
import android.util.Log;

import com.gat.common.util.CommonCheck;
import com.gat.common.util.Strings;
import com.gat.data.firebase.SignInFirebase;
import com.gat.data.response.DataResultListResponse;
import com.gat.data.response.ServerResponse;
import com.gat.data.response.UserResponse;
import com.gat.data.response.impl.Keyword;
import com.gat.data.response.impl.NotifyEntity;
import com.gat.data.response.impl.ResetPasswordResponseData;
import com.gat.data.response.impl.VerifyTokenResponseData;
import com.gat.data.user.EmailLoginData;
import com.gat.feature.bookdetailsender.entity.ChangeStatusResponse;
import com.gat.feature.editinfo.entity.EditInfoInput;
import com.gat.feature.personal.entity.BookChangeStatusInput;
import com.gat.feature.personal.entity.BookInstanceInput;
import com.gat.feature.personal.entity.BookReadingInput;
import com.gat.feature.personal.entity.BookRequestInput;
import com.gat.feature.personal.entity.RequestStatusInput;
import com.gat.feature.personaluser.entity.BookSharingUserInput;
import com.gat.feature.personaluser.entity.BorrowRequestInput;
import com.gat.feature.setting.SocialType;
import com.gat.repository.UserRepository;
import com.gat.repository.datasource.MessageDataSource;
import com.gat.repository.datasource.UserDataSource;
import com.gat.repository.entity.Data;
import com.gat.repository.entity.FirebasePassword;
import com.gat.repository.entity.LoginData;
import com.gat.repository.entity.User;
import com.gat.repository.entity.UserNearByDistance;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

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
    private final Lazy<MessageDataSource> localMessageDataSourceLazy;
    private final Lazy<MessageDataSource> networkMessageDataSourceLazy;
    private final SignInFirebase signInFirebase;

    private final Subject<User> userSubject;

    public UserRepositoryImpl(Lazy<UserDataSource> networkUserDataSourceLazy,
                              Lazy<UserDataSource> localUserDataSourceLazy,
                              Lazy<MessageDataSource> localMessageDataSourceLazy,
                              Lazy<MessageDataSource> networkMessageDataSourceLazy,
                              SignInFirebase signInFirebase) {
        this.networkUserDataSourceLazy = networkUserDataSourceLazy;
        this.localUserDataSourceLazy = localUserDataSourceLazy;
        this.signInFirebase = signInFirebase;
        this.localMessageDataSourceLazy = localMessageDataSourceLazy;
        this.networkMessageDataSourceLazy = networkMessageDataSourceLazy;
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
                    if (data.type() == LoginData.Type.EMAIL) {
                        EmailLoginData emailLoginData = EmailLoginData.instance(
                                ((EmailLoginData)data).email(),
                                ((EmailLoginData)data).password(),
                                ((EmailLoginData)data).name(),
                                ((EmailLoginData)data).image(),
                                ((EmailLoginData)data).type(),
                                response.data().getFirebasePassword()
                        );
                        localUserDataSourceLazy.get().saveLoginData(emailLoginData);
                    } else {
                        localUserDataSourceLazy.get().saveLoginData(data);
                    }
                    return networkUserDataSourceLazy.get().getPersonalInfo();
                })
                .flatMap(rawData -> Observable.just(rawData.getDataReturn(User.typeAdapter(new Gson()))))
                .doOnNext(user -> localUserDataSourceLazy.get().persitUser(user))
                .doOnNext(userSubject::onNext)
                .doOnNext(user -> signInFirebase.login())
        );
    }

    @Override
    public Observable<User> login() {
        return Observable.defer(() -> localUserDataSourceLazy.get().loadLoginData()
                .flatMap(loginData -> networkUserDataSourceLazy.get().login(loginData))
                .flatMap(response -> networkUserDataSourceLazy.get().getPersonalInfo())
                .flatMap(rawData -> Observable.just(rawData.getDataReturn(User.typeAdapter(new Gson()))))
                .doOnNext(userSubject::onNext)
                .doOnNext(user -> signInFirebase.login())
        );
    }

    @Override
    public Observable<Boolean> signOut() {
        return Observable.defer(() -> networkUserDataSourceLazy.get().signOut()
                .flatMap(result -> localMessageDataSourceLazy.get().clearData())
                .flatMap(result -> localUserDataSourceLazy.get().signOut())
                .doOnNext(result -> signInFirebase.signOut())
        );
    }

    @Override
    public Observable<Boolean> loginFirebase() {
        return Observable.defer(() -> localUserDataSourceLazy.get().loadUser()
                .flatMap(user -> {
                    if (user.isValid()) {
                        signInFirebase.login();
                        return signInFirebase.getLoginResult();
                    } else {
                        return Observable.just(false);
                    }
                }));
    }

    @Override
    public Observable<LoginData> getLoginData() {
        return Observable.defer(() -> localUserDataSourceLazy.get().loadLoginData());
    }


    @Override
    public Observable<User> register(LoginData data) {
        return Observable.defer(() -> networkUserDataSourceLazy.get().register(data)
                .flatMap(response -> {
                    localUserDataSourceLazy.get().storeLoginToken(response.data().loginToken());
                    if (data.type() == LoginData.Type.EMAIL) {
                        EmailLoginData emailLoginData = EmailLoginData.instance(
                                ((EmailLoginData)data).email(),
                                ((EmailLoginData)data).password(),
                                ((EmailLoginData)data).name(),
                                ((EmailLoginData)data).image(),
                                ((EmailLoginData)data).type(),
                                response.data().getFirebasePassword()
                        );
                        localUserDataSourceLazy.get().saveLoginData(emailLoginData);
                    } else {
                        localUserDataSourceLazy.get().saveLoginData(data);
                    }
                    return networkUserDataSourceLazy.get().getPersonalInfo();
                })
                .flatMap(rawData -> Observable.just(rawData.getDataReturn(User.typeAdapter(new Gson()))))
                .doOnNext(user -> localUserDataSourceLazy.get().persitUser(user))
                .doOnNext(userSubject::onNext)
                .doOnNext(user -> signInFirebase.register())
        );
    }

    @Override
    public Observable<ServerResponse> sendRequestResetPassword(String email) {
        return Observable.defer(() -> networkUserDataSourceLazy.get().sendRequestResetPassword(email)
                .flatMap(response -> localUserDataSourceLazy.get().storeResetToken(email, response))
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
                    String email = localUserDataSourceLazy.get().getEmailLogin().blockingFirst();
                    EmailLoginData emailLoginData = EmailLoginData.instance(
                            email,
                            password,
                            CommonCheck.getNameFromEmail(email),
                            Strings.EMPTY,
                            LoginData.Type.EMAIL,
                            response.data().getFirebasePassword()
                    );
                    localUserDataSourceLazy.get().saveLoginData(emailLoginData);
                    localUserDataSourceLazy.get().storeLoginToken(response.data().loginToken());
                    return response;
                })
        );
    }

    @Override
    public Observable<List<Address>> getAddress(LatLng location) {
        return Observable.defer(() -> networkUserDataSourceLazy.get().getAddress(location));
    }

    @Override
    public Observable<ServerResponse> updateLocation(String address, LatLng location) {
        return Observable.defer(() -> networkUserDataSourceLazy.get().updateLocation(address, (float)location.longitude, (float)location.latitude))
                .doOnNext(response -> networkUserDataSourceLazy.get().getPersonalInfo()
                        .doOnNext(rawData -> Observable.just(rawData.getDataReturn(User.typeAdapter(new Gson())))));
    }

    @Override
    public Observable<ServerResponse> updateCategories(List<Integer> categories) {
        return Observable.defer(() -> networkUserDataSourceLazy.get().updateCategories(categories))
                .doOnNext(response -> networkUserDataSourceLazy.get().getPersonalInfo()
                        .doOnNext(rawData -> Observable.just(rawData.getDataReturn(User.typeAdapter(new Gson())))));
    }

    @Override
    public Observable<DataResultListResponse<UserNearByDistance>> getPeopleNearByUser(LatLng userLocation, LatLng neLocation, LatLng wsLocation, int page, int sizeOfPage) {
        return Observable.defer(()
                -> networkUserDataSourceLazy.get().getPeopleNearByUserByDistance(
                        (float) userLocation.longitude, (float) userLocation.latitude,
                        (float) neLocation.longitude, (float) neLocation.latitude,
                        (float) wsLocation.longitude, (float) wsLocation.latitude, page, sizeOfPage));
    }

    @Override
    public Observable<DataResultListResponse<UserResponse>> searchUser(String name, int page, int sizeOfPage) {
        return Observable.defer(() -> networkUserDataSourceLazy.get().searchUser(name, page, sizeOfPage));
    }
    @Override
    public Observable<Data> getBookRequest(BookRequestInput input) {
        return Observable.defer( () -> networkUserDataSourceLazy.get().getBookRequest(input));
    }

    @Override
    public Observable<String> changeBookSharingStatus(BookChangeStatusInput input) {
        return Observable.defer( () -> networkUserDataSourceLazy.get().changeBookSharingStatus(input));
    }

    @Override
    public Observable<Data> getReadingBooks(BookReadingInput input) {
        return Observable.defer(()->networkUserDataSourceLazy.get().getReadingBook(input));
    }

    @Override
    public Observable<Data> getBookInstance(BookInstanceInput input) {
        return Observable.defer( () -> networkUserDataSourceLazy.get().getBookInstance(input));
    }

    @Override
    public Observable<String> updateUserInfo(EditInfoInput input) {
        return Observable.defer( () -> networkUserDataSourceLazy.get().updateUserInfo(input));
    }

    @Override
    public Observable<Data> getBookUserSharing(BookSharingUserInput input) {
        return Observable.defer(() -> localUserDataSourceLazy.get().loadUser())
                .map(user -> {
                    input.setUserId(user.userId());
                    return input;
                })
                .flatMap(newInput -> networkUserDataSourceLazy.get().getBookUserSharing(newInput));
//        return Observable.defer( () -> networkUserDataSourceLazy.get().getBookUserSharing(input));
    }

    @Override
    public Observable<Data> getBookDetail(Integer input) {
        return Observable.defer( () -> networkUserDataSourceLazy.get().getBookDetail(input));
    }

    @Override
    public Observable<List<Keyword>> getUsersSearchedKeyword() {
        return Observable.defer(()->networkUserDataSourceLazy.get().getUsersSearchedKeyword());
    }

    @Override
    public Observable<Data<User>> getPersonalData() {
        return Observable.defer(()->networkUserDataSourceLazy.get().getPersonalInfo());
    }

    @Override
    public Observable<User> getUserPublicInfo(int userId) {
        return Observable.defer(() -> networkUserDataSourceLazy.get().getUserInformation(userId))
                /*.flatMap(user -> localUserDataSourceLazy.get().storePublicUserInfo(user))*/;
    }

    @Override
    public Observable<DataResultListResponse<NotifyEntity>> getUserNotification(int page, int per_page) {
        return Observable.defer( () -> networkUserDataSourceLazy.get().getUserNotification(page, per_page));
    }

    @Override
    public Observable<ChangeStatusResponse> requestBookByBorrowrer(RequestStatusInput input) {
        return Observable.defer(() -> networkUserDataSourceLazy.get().requestBookByBorrower(input));
    }

    @Override
    public Observable<ChangeStatusResponse> requestBookByOwner(RequestStatusInput input) {
        return Observable.defer(() -> networkUserDataSourceLazy.get().requestBookByOwner(input));
    }

    @Override
    public Observable<Data> requestBorrowBook(BorrowRequestInput input) {
        return Observable.defer(() -> networkUserDataSourceLazy.get().requestBorrowBook(input))
                .doOnNext(data -> {
                    networkMessageDataSourceLazy.get().makeNewGroup(input.getOwnerId()).subscribe(result -> {
                        Log.d("MakeNewGroup:", "Result " + result);
                    }, throwable -> {
                        throwable.printStackTrace();
                    });
                });
    }

    @Override
    public Observable<ServerResponse> unlinkSocialAccount(int socialType) {
        return Observable.defer(() -> networkUserDataSourceLazy.get().unlinkSocialAccount(socialType))
                .map(serverResponse -> {

                    User user = localUserDataSourceLazy.get().loadUser().blockingFirst();

                    User userStore = null;
                    switch (socialType) {
                        case SocialType.FACEBOOK:
                            userStore = User.builder().userId(user.userId()).email(Strings.EMPTY)
                                    .faceBookId(Strings.EMPTY).faceBookName(Strings.EMPTY)
                                    .userId(user.userId())
                                    .name(user.name())
                                    .imageId(user.imageId())
                                    .userTypeFlag(user.userTypeFlag())
                                    .deleteFlag(user.deleteFlag())
                                    .requestCount(user.requestCount())
                                    .loanCount(user.loanCount())
                                    .readCount(user.readCount())
                                    .passwordFlag(user.passwordFlag())
                                    .googleId(user.googleId())
                                    .googleName(user.googleName())
                                    .twitterId(user.twitterId())
                                    .twitterName(user.twitterName()).build();
                            break;

                        case SocialType.GOOGLE:
                            userStore = User.builder().userId(user.userId()).email(Strings.EMPTY)
                                    .googleId(Strings.EMPTY).googleName(Strings.EMPTY)
                                    .userId(user.userId())
                                    .name(user.name())
                                    .imageId(user.imageId())
                                    .userTypeFlag(user.userTypeFlag())
                                    .deleteFlag(user.deleteFlag())
                                    .requestCount(user.requestCount())
                                    .loanCount(user.loanCount())
                                    .readCount(user.readCount())
                                    .passwordFlag(user.passwordFlag())
                                    .faceBookId(user.faceBookId())
                                    .faceBookName(user.faceBookName())
                                    .twitterId(user.twitterId())
                                    .twitterName(user.twitterName()).build();
                            break;

                        case SocialType.TWITTER:
                            userStore = User.builder().userId(user.userId()).email(Strings.EMPTY)
                                    .twitterId(Strings.EMPTY).twitterName(Strings.EMPTY)
                                    .userId(user.userId())
                                    .name(user.name())
                                    .imageId(user.imageId())
                                    .userTypeFlag(user.userTypeFlag())
                                    .deleteFlag(user.deleteFlag())
                                    .requestCount(user.requestCount())
                                    .loanCount(user.loanCount())
                                    .readCount(user.readCount())
                                    .passwordFlag(user.passwordFlag())
                                    .faceBookId(user.faceBookId())
                                    .faceBookName(user.faceBookName())
                                    .googleId(user.googleId())
                                    .googleName(user.googleName())
                                    .build();
                            break;

                    }
                    localUserDataSourceLazy.get().persitUser(userStore);

                    return serverResponse;
                });
    }

    @Override
    public Observable<ServerResponse> linkSocialAccount(String socialID, String socialName, int socialType) {
//        return Observable.defer(() -> networkUserDataSourceLazy.get().linkSocialAccount(socialID, socialName, socialType));

        return Observable.defer(() -> networkUserDataSourceLazy.get().linkSocialAccount(socialID, socialName, socialType))
                .map(serverResponse -> {

                    User user = localUserDataSourceLazy.get().loadUser().blockingFirst();

                    User userStore = null;
                    switch (socialType) {
                        case SocialType.FACEBOOK:
                            userStore = User.builder().userId(user.userId()).email(Strings.EMPTY)
                                    .faceBookId(socialID).faceBookName(socialName)
                                    .userId(user.userId())
                                    .name(user.name())
                                    .imageId(user.imageId())
                                    .userTypeFlag(user.userTypeFlag())
                                    .deleteFlag(user.deleteFlag())
                                    .requestCount(user.requestCount())
                                    .loanCount(user.loanCount())
                                    .readCount(user.readCount())
                                    .passwordFlag(user.passwordFlag())
                                    .googleId(user.googleId())
                                    .googleName(user.googleName())
                                    .twitterId(user.twitterId())
                                    .twitterName(user.twitterName()).build();
                            break;

                        case SocialType.GOOGLE:
                            userStore = User.builder().userId(user.userId()).email(Strings.EMPTY)
                                    .googleId(socialID).googleName(socialName)
                                    .userId(user.userId())
                                    .name(user.name())
                                    .imageId(user.imageId())
                                    .userTypeFlag(user.userTypeFlag())
                                    .deleteFlag(user.deleteFlag())
                                    .requestCount(user.requestCount())
                                    .loanCount(user.loanCount())
                                    .readCount(user.readCount())
                                    .passwordFlag(user.passwordFlag())
                                    .googleId(user.googleId())
                                    .googleName(user.googleName())
                                    .twitterId(user.twitterId())
                                    .twitterName(user.twitterName()).build();
                            break;

                        case SocialType.TWITTER:
                            userStore = User.builder().userId(user.userId()).email(Strings.EMPTY)
                                    .twitterId(socialID).twitterName(socialName)
                                    .userId(user.userId())
                                    .name(user.name())
                                    .imageId(user.imageId())
                                    .userTypeFlag(user.userTypeFlag())
                                    .deleteFlag(user.deleteFlag())
                                    .requestCount(user.requestCount())
                                    .loanCount(user.loanCount())
                                    .readCount(user.readCount())
                                    .passwordFlag(user.passwordFlag())
                                    .googleId(user.googleId())
                                    .googleName(user.googleName())
                                    .faceBookId(user.faceBookId())
                                    .faceBookName(user.faceBookName())
                                    .build();
                            break;

                    }
                    localUserDataSourceLazy.get().persitUser(userStore);


                    return serverResponse;
                });

    }

    @Override
    public Observable<ServerResponse<FirebasePassword>> addEmailPassword(String email, String password) {
        return Observable.defer(() -> networkUserDataSourceLazy.get().addEmailPassword(email, password));
    }

    @Override
    public Observable<ServerResponse> changeOldPassword(String newPassword, String oldPassword) {
        return Observable.defer(() -> networkUserDataSourceLazy.get().changeOldPassword(newPassword, oldPassword));
    }

}
