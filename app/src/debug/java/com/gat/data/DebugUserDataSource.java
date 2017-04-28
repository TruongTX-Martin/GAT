package com.gat.data;

import android.location.Address;
import android.util.Log;

import com.gat.common.adapter.Item;
import com.gat.common.util.MZDebug;
import com.gat.data.api.GatApi;
import com.gat.data.exception.CommonException;
import com.gat.data.exception.LoginException;
import com.gat.data.id.LongId;
import com.gat.data.response.BookResponse;
import com.gat.data.response.DataResultListResponse;
import com.gat.data.response.ServerResponse;
import com.gat.data.response.UserResponse;
import com.gat.data.response.impl.LoginResponseData;
import com.gat.data.response.impl.NotifyEntity;
import com.gat.data.response.impl.ResetPasswordResponseData;
import com.gat.data.response.ResultInfoList;
import com.gat.data.response.impl.VerifyTokenResponseData;
import com.gat.data.user.EmailLoginData;
import com.gat.data.user.SocialLoginData;
import com.gat.dependency.DataComponent;
import com.gat.feature.editinfo.entity.EditInfoInput;
import com.gat.feature.personal.entity.BookChangeStatusInput;
import com.gat.feature.personal.entity.BookInstanceInput;
import com.gat.feature.personal.entity.BookReadingInput;
import com.gat.feature.personal.entity.BookRequestInput;
import com.gat.feature.personaluser.entity.BookSharingUserInput;
import com.gat.repository.datasource.UserDataSource;
import com.gat.repository.entity.Data;
import com.gat.repository.entity.LoginData;
import com.gat.repository.entity.User;
import com.gat.repository.entity.UserNearByDistance;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableTransformer;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.Subject;
import retrofit2.Response;

/**
 * Created by Rey on 2/23/2017.
 */

public class DebugUserDataSource implements UserDataSource {
    private final String TAG = DebugUserDataSource.class.getSimpleName();

    private final DataComponent dataComponent;

    public DebugUserDataSource(DataComponent dataComponent) {
        this.dataComponent = dataComponent;
    }

    @Override
    public Observable<User> getUserInformation(int userId) {
        GatApi api = dataComponent.getPublicGatApi();
        Observable<Response<ServerResponse<Data<User>>>> responseObservable;
        responseObservable = api.getUserPublicInfo(userId);
        return responseObservable.map(response -> {
            //Log.d(TAG, "UserId" + userId);
            User user = User.NONE;
            ServerResponse<Data<User>> serverResponse = response.body();
            if (serverResponse == null) {

            } else {
                user = serverResponse.data().getDataReturn(User.typeAdapter(new Gson()));
                if (user == null) {
                    user = User.NONE;
                }
            }
            return user;
        });
    }

    @Override
    public Observable<List<User>> getListUserInfo(List<Integer> userIdList) {
        List<User> users = new ArrayList<>();
        Subject<List<User>> userListSubject = BehaviorSubject.create();
        Subject<User> userSubject = BehaviorSubject.create();
        userSubject.subscribe(user -> {
            users.add(user);
            if (users.size() >= userIdList.size())
                userListSubject.onNext(users);
        });
        for (Iterator<Integer> iterator = userIdList.iterator(); iterator.hasNext();) {
            getUserInformation(iterator.next()).subscribe(user -> {
                userSubject.onNext(user);
            });
        }

        return userListSubject;
    }

    @Override
    public Observable<ServerResponse<LoginResponseData>> login(LoginData data) {
        GatApi api = dataComponent.getPublicGatApi();

        Observable<Response<ServerResponse<LoginResponseData>>> response;
        if (data == LoginData.EMPTY || data == null) {
            throw new LoginException(ServerResponse.NO_LOGIN);
        } else if (data.type() == LoginData.Type.EMAIL) {
            EmailLoginData emailLoginData = (EmailLoginData) data;
            response = api.loginByEmail(emailLoginData.email(), emailLoginData.password());
        } else {
            SocialLoginData socialLoginData = (SocialLoginData) data;
            response = api.loginBySocial(socialLoginData.socialID(), Integer.toString(socialLoginData.type()));
        }
        return response.map(result -> {
            ServerResponse<LoginResponseData> sr = result.body();
            if (sr == null) {
                Log.d(TAG, result.toString());
                Log.d(TAG, result.raw().toString());
                Log.d(TAG, result.message());
                Log.d(TAG, Integer.toString(result.code()));
                sr = ServerResponse.BAD_RESPONSE;
                sr.code(result.code());
                throw new LoginException(sr);
            } else {
                Log.d(TAG, sr.message());
                Log.d(TAG, sr.data().loginToken());
                sr.code(result.code());
                if (!sr.isOk())
                    throw new LoginException(sr);
            }
            return sr;
        });
    }

    public Observable<ServerResponse<LoginResponseData>> register(LoginData loginData) {
        Log.d(TAG, "register");
        GatApi api = dataComponent.getPublicGatApi();

        Observable<Response<ServerResponse<LoginResponseData>>> response;
        if (loginData.type() == LoginData.Type.EMAIL) {
            EmailLoginData emailLoginData = (EmailLoginData) loginData;
            Log.d(TAG, emailLoginData.email() + "," + emailLoginData.password() + "," + emailLoginData.name());
            response = api.registerByEmail(emailLoginData.email(), emailLoginData.password(), emailLoginData.name());
        } else {
            SocialLoginData socialLoginData = (SocialLoginData) loginData;
            response = api.registerBySocial(
                    socialLoginData.socialID(),
                    Integer.toString(socialLoginData.type()),
                    socialLoginData.name(),
                    socialLoginData.email(),
                    socialLoginData.password()/*,
                        image*/);
        }
        ObservableTransformer<Response<ServerResponse<LoginResponseData>>, ServerResponse<LoginResponseData>> transformer =
                upstream -> upstream.map(result -> {
                    ServerResponse<LoginResponseData> sr = result.body();
                    if (sr == null) {
                        Log.d(TAG, result.toString());
                        Log.d(TAG, result.raw().toString());
                        Log.d(TAG, result.message());
                        Log.d(TAG, Integer.toString(result.code()));
                        sr = ServerResponse.BAD_RESPONSE;
                        sr.code(result.code());
                        throw new LoginException(sr);
                    } else {
                        Log.d(TAG, sr.message());
                        Log.d(TAG, sr.data().loginToken());
                        sr.code(result.code());
                        if (!sr.isOk())
                            throw new LoginException(sr);
                    }
                    return sr;
                });
        return response.compose(transformer);
    }

    @Override
    public Observable<ServerResponse<ResetPasswordResponseData>> sendRequestResetPassword(String email) {
        Log.d(TAG, "sendRequestReset");
        GatApi api = dataComponent.getPublicGatApi();

        Observable<Response<ServerResponse<ResetPasswordResponseData>>> responseObservable;
        responseObservable = api.requestResetPassword(email);
        return responseObservable.map(response -> {
            ServerResponse<ResetPasswordResponseData> serverResponse = response.body();
            if (serverResponse == null) {
                serverResponse = ServerResponse.BAD_RESPONSE;
            }
            serverResponse.code(response.code());
            return serverResponse;
        });

    }

    @Override
    public Observable<ServerResponse<VerifyTokenResponseData>> verifyToken(String code, String tokenReset) {
        Log.d(TAG, "verifyToken");
        GatApi api = dataComponent.getPublicGatApi();

        Observable<Response<ServerResponse<VerifyTokenResponseData>>> responseObservable;
        responseObservable = api.verifyResetToken(code, tokenReset);

        Log.d(TAG, "code:" + code + ",token:" + tokenReset);

        return responseObservable.map(response -> {
            ServerResponse<VerifyTokenResponseData> serverResponse = response.body();
            if (serverResponse == null) {
                serverResponse = ServerResponse.BAD_RESPONSE;
            }
            serverResponse.code(response.code());
            return serverResponse;
        });

    }

    @Override
    public Observable<ServerResponse<LoginResponseData>> changePassword(String password, String tokenVerified) {
        Log.d(TAG, "changePassword");
        GatApi api = dataComponent.getPublicGatApi();

        Observable<Response<ServerResponse<LoginResponseData>>> responseObservable;

        responseObservable = api.resetPassword(password, tokenVerified);

        Log.d(TAG, "password:" + password + ",token:" + tokenVerified);

        return responseObservable.map(response -> {
            ServerResponse<LoginResponseData> serverResponse = response.body();
            if (serverResponse == null) {
                serverResponse = ServerResponse.BAD_RESPONSE;
            }
            serverResponse.code(response.code());
            return serverResponse;
        });

    }

    @Override
    public Observable<ServerResponse> updateLocation(String address, float longitude, float latitude) {
        Log.d(TAG, "updateLocation:" + address + "," + longitude + "," + latitude);
        GatApi api = dataComponent.getPrivateGatApi();

        Observable<Response<ServerResponse>> responseObservable;
        responseObservable = api.updateLocation(address, longitude, latitude);
        return responseObservable.map(response -> {
            ServerResponse serverResponse = response.body();
            if (serverResponse == null) {
                serverResponse = ServerResponse.BAD_RESPONSE;
            }
            serverResponse.code(response.code());
            return serverResponse;
        });
    }

    @Override
    public Observable<ServerResponse> updateCategories(List<Integer> categories) {
        Log.d(TAG, "updateCategories");
        GatApi api = dataComponent.getPrivateGatApi();

        Observable<Response<ServerResponse>> responseObservable;
        responseObservable = api.updateCategory(categories);
        return responseObservable.map(response -> {
            ServerResponse serverResponse = response.body();
            if (serverResponse == null) {
                serverResponse = ServerResponse.BAD_RESPONSE;
            }
            serverResponse.code(response.code());
            return serverResponse;
        });
    }

    @Override
    public Observable<List<UserNearByDistance>> getPeopleNearByUserByDistance(
            float currentLongitude, float currentLatitude,
            float neLongitude, float neLatitude, float wsLongitude, float wsLatitude, int page, int sizeOfPage) {
        MZDebug.d(TAG, "getPeopleNearByUserByDistance");

        GatApi api = dataComponent.getPublicGatApi();
        Observable<Response<ServerResponse<ResultInfoList<UserNearByDistance>>>> responseObservable;
        responseObservable = api.getPeopleNearByUser(currentLatitude, currentLongitude,
                neLatitude, neLongitude, wsLatitude, wsLongitude, page, sizeOfPage);

        return responseObservable.map(response -> {
            List<UserNearByDistance> list = response.body().data().getResultInfo();
            return list;
        });
    }


    @Override
    public Observable<DataResultListResponse<UserResponse>> searchUser
            (String name, int page, int sizeOfPage) {
        MZDebug.i("_____________________________________ searchUser _____________________________");

        GatApi api = dataComponent.getPublicGatApi();
        Observable<Response<ServerResponse<DataResultListResponse<UserResponse>>>> responseObservable;
        responseObservable = api.searchUser(name, page, sizeOfPage);

        return responseObservable.map(response -> {
            DataResultListResponse<UserResponse> data = response.body().data();
            return data;
        });
    }

    @Override
    public Observable<Data> getBookRequest(BookRequestInput input) {
        GatApi api = dataComponent.getPrivateGatApi();
        Observable<Response<ServerResponse<Data>>> responseObservable = api.getBookRequest(input.getParamSharing(), input.getParamBorrow(), input.getPage(), input.getPer_page());
        return responseObservable.map(response -> {
            ServerResponse<Data> serverResponse = response.body();
            if (serverResponse == null) {
                serverResponse = ServerResponse.BAD_RESPONSE;
            }
            serverResponse.code(response.code());
            return serverResponse.data();
        });
    }

    @Override
    public Observable<Data> changeBookSharingStatus(BookChangeStatusInput input) {
        GatApi api = dataComponent.getPrivateGatApi();
        Observable<Response<ServerResponse<Data>>> responseObservable = api.changeBookSharingStatus(input.getInstanceId(), input.getSharingStatus());
        return responseObservable.map(response -> {
            ServerResponse<Data> serverResponse = response.body();
            if (serverResponse == null) {
                serverResponse = ServerResponse.BAD_RESPONSE;
            }
            serverResponse.code(response.code());
            return serverResponse.data();
        });
    }

    @Override
    public Observable<List<String>> getUsersSearchedKeyword() {
        MZDebug.i("________________________ getUsersSearchedKeyword ___________________________");

        GatApi api = dataComponent.getPrivateGatApi();
        Observable<Response<ServerResponse<ResultInfoList<String>>>> responseObservable;
        responseObservable = api.getUsersSearchedKeyword();

        List<String> list = new ArrayList<String>();
        list.add("user 1");
        list.add("user 2");
        list.add("user 3");
        list.add("user 4");
        list.add("user 5");

        return responseObservable.map(response -> {
            //            List<String> list = response.body().data().getResultInfo();
            return list;
        });
    }

    @Override
    public Observable<Data<User>> getPersonalInfo() {
        GatApi api = dataComponent.getPrivateGatApi();
        Observable<Response<ServerResponse<Data<User>>>> responseObservable = api.getPersonalInformation();
        return responseObservable.map(response -> {
            ServerResponse<Data<User>> serverResponse = response.body();
            if (serverResponse == null) {
                serverResponse = ServerResponse.BAD_RESPONSE;
            }
            serverResponse.code(response.code());
            return serverResponse.data();
        });
    }

    @Override
    public Observable<Data> getBookInstance(BookInstanceInput input) {
        GatApi api = dataComponent.getPrivateGatApi();
        Observable<Response<ServerResponse<Data>>> responseObservable = api.getBookInstance(input.isSharingFilter(), input.isNotSharingFilter(), input.isLostFilter(), input.getPage(), input.getPer_page());
        return responseObservable.map(response -> {
            ServerResponse<Data> serverResponse = response.body();
            if (serverResponse == null) {
                serverResponse = ServerResponse.BAD_RESPONSE;
            }
            serverResponse.code(response.code());
            return serverResponse.data();
        });
    }

    @Override
    public Observable<Data> getReadingBook(BookReadingInput input) {
        GatApi api = dataComponent.getPrivateGatApi();
        Observable<Response<ServerResponse<Data>>> responseObservable = api.getReadingBooks(input.getUserId(), input.isReadingFilter(), input.isToReadFilter(), input.isReadFilter(), input.getPage(), input.getPer_page());
        return responseObservable.map(response -> {
            ServerResponse<Data> serverResponse = response.body();
            if (serverResponse == null) {
                serverResponse = ServerResponse.BAD_RESPONSE;
            }
            serverResponse.code(response.code());
            return serverResponse.data();
        });

    }

    @Override
    public Observable<Data> updateUserInfo(EditInfoInput input) {
        GatApi api = dataComponent.getPrivateGatApi();
        Observable<Response<ServerResponse<Data>>> responseObservable = api.updateUserInfo(input.getName(),input.getImageBase64(),input.isChangeImage());
        return responseObservable.map(response -> {
            ServerResponse<Data> serverResponse = response.body();
            if (serverResponse == null) {
                serverResponse = ServerResponse.BAD_RESPONSE;
            }
            serverResponse.code(response.code());
            return serverResponse.data();
        });
    }

    @Override
    public Observable<Data> getBookUserSharing(BookSharingUserInput input) {
        GatApi api = dataComponent.getPrivateGatApi();
        Observable<Response<ServerResponse<Data>>> responseObservable = api.getBookUserSharing(input.getUserId(),input.getOwnerId(),input.getPage(),input.getPer_page());
        return responseObservable.map(response -> {
            ServerResponse<Data> serverResponse = response.body();
            if (serverResponse == null) {
                serverResponse = ServerResponse.BAD_RESPONSE;
            }
            serverResponse.code(response.code());
            return serverResponse.data();
        });
    }

    @Override
    public Observable<Data> getBookDetail(Integer input) {
        GatApi api = dataComponent.getPrivateGatApi();
        Observable<Response<ServerResponse<Data>>> responseObservable = api.getBookDetail(input);
        return responseObservable.map(response -> {
            ServerResponse<Data> serverResponse = response.body();
            if (serverResponse == null) {
                serverResponse = ServerResponse.BAD_RESPONSE;
            }
            serverResponse.code(response.code());
            return serverResponse.data();
        });
    }

    @Override
    public Observable<DataResultListResponse<NotifyEntity>> getUserNotification(int page, int per_page) {
        MZDebug.w("______________________________ getUserNotification ___________________________");

        GatApi api = dataComponent.getPrivateGatApi();
        Observable<Response<ServerResponse<DataResultListResponse<NotifyEntity>>>> responseObservable =
                api.getUserNotification(page, per_page);

        return responseObservable.map(response -> {
            ServerResponse<DataResultListResponse<NotifyEntity>> serverResponse = response.body();

            if (null == serverResponse)
                throw new CommonException("Connect to server failed.");

            DataResultListResponse<NotifyEntity> data = new DataResultListResponse<NotifyEntity>();
            data.setTotalResult(20);

            NotifyEntity today, nextDay;

            today =  NotifyEntity.builder().notificationId(1)
                    .notificationType(0)
                    .destId(123)
                    .sourceId(2)
                    .sourceName("Đây là source ")
                    .sourceImage("32507316083")
                    .targetId(2)
                    .targetName("Đây là target namename")
                    .referId(287)
                    .pullFlag(false)
                    .modifyTime("2017-04-26 08:08:10").build();

            nextDay =  NotifyEntity.builder().notificationId(23)
                    .notificationType(1)
                    .destId(2222)
                    .sourceId(2)
                    .sourceName("Đây là source ")
                    .sourceImage("32507316083")
                    .targetId(2)
                    .targetName("Đây là target namename")
                    .referId(287)
                    .pullFlag(false)
                    .modifyTime("2017-04-27 12:12:12").build();

            List<NotifyEntity> list = new ArrayList<>();
            list.add(today);
            list.add(nextDay);

            data.setResultInfo(list);

            MZDebug.w("getUserNotification, total notifies: " + data.getNotifyTotal());

            return data;
        });
    }


    @Override
    public void storeLoginToken(String token) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Observable<String> getLoginToken() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Observable<LoginData> loadLoginData() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void saveLoginData(LoginData loginData) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Observable<User> loadUser() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Observable<User> persitUser(User user) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Observable<ServerResponse<VerifyTokenResponseData>> storeVerifyToken
            (ServerResponse<VerifyTokenResponseData> data) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Observable<ServerResponse<VerifyTokenResponseData>> getVerifyToken() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Observable<ServerResponse<ResetPasswordResponseData>> storeResetToken
            (ServerResponse<ResetPasswordResponseData> data) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Observable<ServerResponse<ResetPasswordResponseData>> getResetToken() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Observable<List<Address>> getAddress(LatLng location) {
        // TODO
        throw new UnsupportedOperationException();
    }

}
