package com.gat.data;

import android.graphics.Bitmap;
import android.location.Address;
import android.util.Log;

import com.gat.common.util.ClientUtils;
import com.gat.common.util.CommonCheck;
import com.gat.common.util.MZDebug;
import com.gat.common.util.Strings;
import com.gat.data.api.GatApi;
import com.gat.data.exception.CommonException;
import com.gat.data.exception.LoginException;
import com.gat.data.response.DataResultListResponse;
import com.gat.data.response.ResultInfoList;
import com.gat.data.response.ServerResponse;
import com.gat.data.response.SimpleResponse;
import com.gat.data.response.UserResponse;
import com.gat.data.response.impl.Keyword;
import com.gat.data.response.impl.LoginResponseData;
import com.gat.data.response.impl.NotifyEntity;
import com.gat.data.response.impl.ResetPasswordResponseData;
import com.gat.data.response.impl.VerifyTokenResponseData;
import com.gat.data.user.EmailLoginData;
import com.gat.data.user.SocialLoginData;
import com.gat.dependency.DataComponent;
import com.gat.feature.bookdetailsender.entity.ChangeStatusResponse;
import com.gat.feature.editinfo.entity.EditInfoInput;
import com.gat.feature.personal.entity.BookChangeStatusInput;
import com.gat.feature.personal.entity.BookInstanceInput;
import com.gat.feature.personal.entity.BookReadingInput;
import com.gat.feature.personal.entity.BookRequestInput;
import com.gat.feature.personal.entity.RequestStatusInput;
import com.gat.feature.personaluser.entity.BookSharingUserInput;
import com.gat.feature.personaluser.entity.BorrowRequestInput;
import com.gat.repository.datasource.UserDataSource;
import com.gat.repository.entity.Data;
import com.gat.repository.entity.FirebasePassword;
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
            ServerResponse<LoginResponseData> sr = CommonCheck.checkResponse(result);
            sr.code(result.code());
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
            Bitmap bitmap = ClientUtils.getBitmapFromURL(socialLoginData.image());
            response = api.registerBySocial(
                    socialLoginData.socialID(),
                    Integer.toString(socialLoginData.type()),
                    socialLoginData.name(),
                    socialLoginData.email(),
                    socialLoginData.password(),
                    bitmap == null ? Strings.EMPTY : ClientUtils.imageEncode64(bitmap));
        }
        ObservableTransformer<Response<ServerResponse<LoginResponseData>>, ServerResponse<LoginResponseData>> transformer =
                upstream -> upstream.map(result -> {
                    ServerResponse<LoginResponseData> sr = CommonCheck.checkResponse(result);
                    Log.d(TAG, sr.message());
                    Log.d(TAG, sr.data().loginToken());
                    sr.code(result.code());
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
            ServerResponse<ResetPasswordResponseData> serverResponse = CommonCheck.checkResponse(response);
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
            ServerResponse<VerifyTokenResponseData> serverResponse = CommonCheck.checkResponse(response);
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
            ServerResponse<LoginResponseData> serverResponse = CommonCheck.checkResponse(response);
            serverResponse.code(response.code());
            return serverResponse;
        });

    }

    @Override
    public Observable<ServerResponse> updateLocation(String address, float longitude, float latitude) {
        Log.d(TAG, "updateLocation:" + address + "," + longitude + "," + latitude);
        GatApi api = dataComponent.getPrivateGatApi();

        Observable<Response<ServerResponse<SimpleResponse>>> responseObservable;
        responseObservable = api.updateLocation(address, longitude, latitude);
        return responseObservable.map(response -> {
            ServerResponse serverResponse = CommonCheck.checkResponse(response);
            serverResponse.code(response.code());
            return serverResponse;
        });
    }

    @Override
    public Observable<ServerResponse> updateCategories(List<Integer> categories) {
        Log.d(TAG, "updateCategories");
        GatApi api = dataComponent.getPrivateGatApi();

        Observable<Response<ServerResponse<SimpleResponse>>> responseObservable;
        responseObservable = api.updateCategory(categories);
        return responseObservable.map(response -> {
            ServerResponse serverResponse = CommonCheck.checkResponse(response);
            serverResponse.code(response.code());
            return serverResponse;
        });
    }

    @Override
    public Observable<DataResultListResponse<UserNearByDistance>> getPeopleNearByUserByDistance(
            float currentLongitude, float currentLatitude,
            float neLongitude, float neLatitude, float wsLongitude, float wsLatitude, int page, int sizeOfPage) {
        MZDebug.d(TAG, "getPeopleNearByUserByDistance");

        GatApi api = dataComponent.getPublicGatApi();
        Observable<Response<ServerResponse<DataResultListResponse<UserNearByDistance>>>> responseObservable;
        responseObservable = api.getPeopleNearByUser(currentLatitude, currentLongitude,
                neLatitude, neLongitude, wsLatitude, wsLongitude, page, sizeOfPage);

        return responseObservable.map(response -> response.body().data());
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
    public Observable<DataResultListResponse<UserResponse>> searchUserTotal(String name, int userId) {
        MZDebug.i("___________________________________ searchUserTotal __________________________");

        GatApi api = dataComponent.getPublicGatApi();
        Observable<Response<ServerResponse<DataResultListResponse>>> responseObservable;
        responseObservable = api.searchUserTotal(name, userId);

        return responseObservable.map( response -> {
            DataResultListResponse data = response.body().data();
            return data;
        });
    }

    @Override
    public Observable<Data> getBookRequest(BookRequestInput input) {
        GatApi api = dataComponent.getPrivateGatApi();
        Observable<Response<ServerResponse<Data>>> responseObservable = api.getBookRequest(input.getParamSharing(), input.getParamBorrow(), input.getPage(), input.getPer_page());
        return responseObservable.map(response -> {
            ServerResponse<Data> serverResponse = CommonCheck.checkResponse(response);
            serverResponse.code(response.code());
            return serverResponse.data();
        });
    }

    @Override
    public Observable<String> changeBookSharingStatus(BookChangeStatusInput input) {
        GatApi api = dataComponent.getPrivateGatApi();
        Observable<Response<ServerResponse<Data>>> responseObservable = api.changeBookSharingStatus(input.getInstanceId(), input.getSharingStatus());
        return responseObservable.map(response -> {
            ServerResponse<Data> serverResponse = CommonCheck.checkResponse(response);
            serverResponse.code(response.code());
            return serverResponse.message();
        });
    }

    @Override
    public Observable<List<Keyword>> getUsersSearchedKeyword() {
        MZDebug.i("________________________ getUsersSearchedKeyword ___________________________");

        GatApi api = dataComponent.getPrivateGatApi();
        Observable<Response<ServerResponse<ResultInfoList<Keyword>>>> responseObservable;
        responseObservable = api.getUsersSearchedKeyword();

//        List<String> list = new ArrayList<String>();
//        list.add("user 1");
//        list.add("user 2");
//        list.add("user 3");
//        list.add("user 4");
//        list.add("user 5");

        return responseObservable.map(response -> {
            List<Keyword> list = response.body().data().getResultInfo();
            return list;
        });
    }

    @Override
    public Observable<Data<User>> getPersonalInfo() {
        GatApi api = dataComponent.getPrivateGatApi();
        Observable<Response<ServerResponse<Data<User>>>> responseObservable = api.getPersonalInformation();
        return responseObservable.map(response -> {
            ServerResponse<Data<User>> serverResponse = CommonCheck.checkResponse(response);
            serverResponse.code(response.code());
            return serverResponse.data();
        });
    }

    @Override
    public Observable<Data> getBookInstance(BookInstanceInput input) {
        GatApi api = dataComponent.getPrivateGatApi();
        Observable<Response<ServerResponse<Data>>> responseObservable = api.getBookInstance(input.isSharingFilter(), input.isNotSharingFilter(), input.isLostFilter(), input.getPage(), input.getPer_page());
        return responseObservable.map(response -> {
            ServerResponse<Data> serverResponse = CommonCheck.checkResponse(response);
            serverResponse.code(response.code());
            return serverResponse.data();
        });
    }

    @Override
    public Observable<Data> getReadingBook(BookReadingInput input) {
        GatApi api = dataComponent.getPrivateGatApi();
        Observable<Response<ServerResponse<Data>>> responseObservable = api.getReadingBooks(input.getUserId(), input.isReadingFilter(), input.isToReadFilter(), input.isReadFilter(), input.getPage(), input.getPer_page());
        return responseObservable.map(response -> {
            ServerResponse<Data> serverResponse = CommonCheck.checkResponse(response);
            serverResponse.code(response.code());
            return serverResponse.data();
        });

    }

    @Override
    public Observable<String> updateUserInfo(EditInfoInput input) {
        GatApi api = dataComponent.getPrivateGatApi();
        Observable<Response<ServerResponse<Data>>> responseObservable = api.updateUserInfo(input.getName(),input.getImageBase64(),input.isChangeImage());
        return responseObservable.map(response -> {
            ServerResponse<Data> serverResponse = CommonCheck.checkResponse(response);
            serverResponse.code(response.code());
            return response.message();
        });
    }

    @Override
    public Observable<Data> getBookUserSharing(BookSharingUserInput input) {
        GatApi api = dataComponent.getPrivateGatApi();
        Observable<Response<ServerResponse<Data>>> responseObservable = api.getBookUserSharing(input.getUserId(),input.getOwnerId(),input.getPage(),input.getPer_page());
        return responseObservable.map(response -> {
            ServerResponse<Data> serverResponse = CommonCheck.checkResponse(response);
            serverResponse.code(response.code());
            return serverResponse.data();
        });
    }

    @Override
    public Observable<Data> getBookDetail(Integer input) {
        GatApi api = dataComponent.getPrivateGatApi();
        Observable<Response<ServerResponse<Data>>> responseObservable = api.getBookDetail(input);
        return responseObservable.map(response -> {
            ServerResponse<Data> serverResponse = CommonCheck.checkResponse(response);
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

            return serverResponse.data();

//            DataResultListResponse<NotifyEntity> data = new DataResultListResponse<NotifyEntity>();
//            data.setTotalResult(20);
//            NotifyEntity today, today0, today2, nextDay;
//            today0 =  NotifyEntity.builder().notificationId(1)
//                    .notificationType(0)
//                    .destId(123)
//                    .sourceId(2)
//                    .sourceName("Đây là source ")
//                    .sourceImage("32507316083")
//                    .targetId("1")
//                    .targetName("Đây là target namename")
//                    .referId(287)
//                    .pullFlag(false)
//                    .beginTime("2017-04-25 05:08:10").build();
//
//            today =  NotifyEntity.builder().notificationId(1)
//                    .notificationType(0)
//                    .destId(123)
//                    .sourceId(2)
//                    .sourceName("Đây là source ")
//                    .sourceImage("32507316083")
//                    .targetId("1")
//                    .targetName("Đây là target namename")
//                    .referId(287)
//                    .pullFlag(false)
//                    .beginTime("2017-04-26 05:08:10").build();
//            today2 =  NotifyEntity.builder().notificationId(1)
//                    .notificationType(0)
//                    .destId(123)
//                    .sourceId(2)
//                    .sourceName("Đây là source ")
//                    .sourceImage("32507316083")
//                    .targetId("1")
//                    .targetName("Đây là target namename")
//                    .referId(287)
//                    .pullFlag(false)
//                    .beginTime("2017-04-26 08:08:10").build();
//
//            nextDay =  NotifyEntity.builder().notificationId(23)
//                    .notificationType(1)
//                    .destId(2222)
//                    .sourceId(2)
//                    .sourceName("Đây là source ")
//                    .sourceImage("32507316083")
//                    .targetId("2") //
//                    .targetName("Đây là target namename")
//                    .referId(287)
//                    .pullFlag(false)
//                    .beginTime("2017-04-27 12:12:12").build();
//
//            List<NotifyEntity> list = new ArrayList<>();
//            list.add(nextDay);
//            list.add(today2);
//            list.add(today);
//            list.add(today);
//            list.add(today0);
//            data.setResultInfo(list);
//            MZDebug.w("getUserNotification, total notifies: " + data.getNotifyTotal());
//            return data;
        });
    }

        @Override
    public Observable<String> requestBookByBorrower(RequestStatusInput input) {
        GatApi api = dataComponent.getPrivateGatApi();
        Observable<Response<ServerResponse<Data>>> responseObservable = api.requestBookByBorrower(input.getRecordId(),input.getCurrentStatus(),input.getNewStatus());
        return responseObservable.map(response -> {
            ServerResponse<Data> serverResponse = CommonCheck.checkResponse(response);
            serverResponse.code(response.code());
            return serverResponse.message();
        });
    }

    @Override
    public Observable<ChangeStatusResponse> requestBookByOwner(RequestStatusInput input) {
        GatApi api = dataComponent.getPrivateGatApi();
        Observable<Response<ServerResponse<Data>>> responseObservable = api.requestBookByOwner(input.getRecordId(),input.getCurrentStatus(),input.getNewStatus());
        return responseObservable.map(response -> {
            ServerResponse<Data> serverResponse = CommonCheck.checkResponse(response);
            serverResponse.code(response.code());
            ChangeStatusResponse statusResponse = new ChangeStatusResponse();
            statusResponse.setMessage(response.message());
            statusResponse.setStatusCode(response.code());
            return statusResponse;
        });
    }

    @Override
    public Observable<Data> requestBorrowBook(BorrowRequestInput input) {
        GatApi api = dataComponent.getPrivateGatApi();
        Observable<Response<ServerResponse<Data>>> responseObservable = api.requestBorrowBook(input.getEditionId(),input.getOwnerId());
        return responseObservable.map(response -> {
            ServerResponse<Data> serverResponse = response.body();
            if (serverResponse == null) {
                serverResponse = ServerResponse.BAD_RESPONSE;
            }
            serverResponse.code(response.code());

            ServerResponse<Data> sr = CommonCheck.checkResponse(response);
            sr.code(response.code());

            return sr.data();
        });
    }

    @Override
    public Observable<ServerResponse> unlinkSocialAccount(int socialType) {
        GatApi api = dataComponent.getPrivateGatApi();
        Observable<Response<ServerResponse>> responseObservable;
        responseObservable = api.unlinkSocialAccount(socialType);

        return responseObservable.map(response -> {
            return response.body();
        });
    }

    @Override
    public Observable<ServerResponse> linkSocialAccount(String socialID, String socialName, int socialType) {
        MZDebug.w("User Data Srouce: linkSocialAccount");

        GatApi api = dataComponent.getPrivateGatApi();
        Observable<Response<ServerResponse>> responseObservable;
        responseObservable = api.linkSocialAccount(socialID, socialName, socialType);

        return responseObservable.map(response -> {
            return response.body();
        });
    }

    @Override
    public Observable<ServerResponse<FirebasePassword>> addEmailPassword(String email, String password) {
        GatApi api = dataComponent.getPrivateGatApi();
        Observable<Response<ServerResponse<FirebasePassword>>> responseObservable;
        responseObservable = api.addEmailPassword(email, password);

        return responseObservable.map(response -> {
            return response.body();
        });
    }

    @Override
    public Observable<ServerResponse> changeOldPassword(String newPassword, String oldPassword) {
        GatApi api = dataComponent.getPrivateGatApi();
        Observable<Response<ServerResponse>> responseObservable;
        responseObservable = api.updatePassword(oldPassword, newPassword);

        return responseObservable.map(response -> {
            return response.body();
        });
    }

    @Override
    public Observable<String> removeBook(int instanceId) {
        GatApi api = dataComponent.getPrivateGatApi();
        Observable<Response<ServerResponse<Data>>> responseObservable = api.removeBook(instanceId);
        return responseObservable.map(response -> {
            ServerResponse serverResponse = CommonCheck.checkResponse(response);
            serverResponse.code(response.code());
            return serverResponse.message();
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
    public Observable<Boolean> signOut() {
        Log.d(TAG, "SignOut");
        GatApi api = dataComponent.getPrivateGatApi();
        Observable<Response<ServerResponse<SimpleResponse>>> responseObservable = api.signOut();
        return responseObservable.map(response -> {
            ServerResponse serverResponse = CommonCheck.checkResponse(response);
            serverResponse.code(response.code());
            return serverResponse.isOk();
        });
    }

    @Override
    public Observable<Boolean> messageNotification(int receiver, String message) {
        Log.d(TAG, "mesNoti:" + Thread.currentThread().getName());
        GatApi api = dataComponent.getPrivateGatApi();
        Observable<Response<ServerResponse<Boolean>>> responseObservable = api.messageNotification(receiver, message);
        return responseObservable.map(response -> {
            Log.d(TAG, "mesNotiRes:" + Thread.currentThread().getName());
            ServerResponse serverResponse = CommonCheck.checkResponse(response);
            serverResponse.code(response.code());
            return serverResponse.isOk();
        });
    }

    @Override
    public Observable<Boolean> registerFirebaseToken(String token) {
        GatApi api = dataComponent.getPrivateGatApi();
        Observable<Response<ServerResponse<Boolean>>> responseObservable = api.registerFirebaseToken(token);
        return responseObservable.map(response -> {
            ServerResponse serverResponse = CommonCheck.checkResponse(response);
            serverResponse.code(response.code());
            return serverResponse.isOk();
        });
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
    public void persitUser(User user) {
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
            (String email, ServerResponse<ResetPasswordResponseData> data) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Observable<String> getEmailLogin() {
        return null;
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
