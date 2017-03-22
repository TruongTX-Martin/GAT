package com.gat.data;

import android.location.Address;
import android.util.Log;

import com.gat.data.api.GatApi;
import com.gat.data.exception.LoginException;
import com.gat.data.id.LongId;
import com.gat.data.response.ServerResponse;
import com.gat.data.response.impl.LoginResponseData;
import com.gat.data.response.impl.LoginResponseData;
import com.gat.data.response.impl.ResetPasswordResponseData;
import com.gat.data.response.impl.VerifyTokenResponseData;
import com.gat.data.user.EmailLoginData;
import com.gat.data.user.SocialLoginData;
import com.gat.dependency.DataComponent;
import com.gat.repository.datasource.UserDataSource;
import com.gat.repository.entity.LoginData;
import com.gat.repository.entity.User;
import com.google.android.gms.maps.model.LatLng;

import java.io.File;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableTransformer;
import okhttp3.MediaType;
import okhttp3.RequestBody;
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
    public Observable<User> getUserInformation() {
        // TODO
        return Observable.just(User.builder()
                .id(LongId.instance(1))
                .address("asdf")
                .name("ducdt")
                .phoneNumber("1234567890")
                .avatar("qwertyuiop")
                .build());
    }

    @Override
    public Observable<ServerResponse<LoginResponseData>> login(LoginData data) {
        GatApi api = dataComponent.getPublicGatApi();

        Observable<Response<ServerResponse<LoginResponseData>>> response;
        if (data == LoginData.EMPTY || data == null) {
            throw new LoginException(ServerResponse.NO_LOGIN);
        } else if (data.type() == LoginData.Type.EMAIL) {
            EmailLoginData emailLoginData = (EmailLoginData)data;
            response = api.loginByEmail(emailLoginData.email(), emailLoginData.password());
        } else {
            SocialLoginData socialLoginData = (SocialLoginData)data;
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
                Log.d(TAG,sr.message());
                Log.d(TAG,sr.data().loginToken());
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
            EmailLoginData emailLoginData = (EmailLoginData)loginData;
            Log.d(TAG,emailLoginData.email()+","+emailLoginData.password()+","+emailLoginData.name());
            response = api.registerByEmail(emailLoginData.email(), emailLoginData.password(), emailLoginData.name());
        } else {
            SocialLoginData socialLoginData = (SocialLoginData)loginData;
            File file = new File(socialLoginData.image());
            RequestBody image = RequestBody.create(MediaType.parse("image/*"), file);
            response = api.registerBySocial(socialLoginData.socialID(),
                            Integer.toString(socialLoginData.type()),
                            socialLoginData.name(),
                            socialLoginData.email(),
                            socialLoginData.password(),
                            image);
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
                        Log.d(TAG,sr.message());
                        Log.d(TAG,sr.data().loginToken());
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

        Log.d(TAG, "code:" + code + ",token:"+tokenReset);

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

        Log.d(TAG, "password:"+password+",token:"+tokenVerified);

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
        Log.d(TAG, "updateLocation:"+ address + "," + longitude + "," + latitude );
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
    public Observable<ServerResponse<VerifyTokenResponseData>> storeVerifyToken(ServerResponse<VerifyTokenResponseData> data) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Observable<ServerResponse<VerifyTokenResponseData>> getVerifyToken() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Observable<ServerResponse<ResetPasswordResponseData>> storeResetToken(ServerResponse<ResetPasswordResponseData> data) {
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
